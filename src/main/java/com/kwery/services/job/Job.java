package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.conf.KweryDirectory;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.services.job.parameterised.*;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.kwery.models.SqlQueryExecutionModel.Status.*;

//TODO - Refactor this class as per listener design pattern
public class Job implements Callable<JobExecutionModel> {
    protected Logger logger = LoggerFactory.getLogger(Job.class);

    protected final JobModel jobModel;
    protected final JobDao jobDao;
    protected final JobExecutionDao jobExecutionDao;
    protected final Map<String, ?> parameters;
    private final SqlQueryDao sqlQueryDao;
    private final SqlQueryExecutionDao sqlQueryExecutionDao;
    private final DatasourceService datasourceService;
    private final ResultSetProcessorFactory resultSetProcessorFactory;
    private final PreparedStatementExecutorFactory preparedStatementExecutorFactory;
    protected final KweryDirectory kweryDirectory;
    protected final SqlQueryNormalizerFactory sqlQueryNormalizerFactory;
    protected final SqlQueryParameterExtractorFactory sqlQueryParameterExtractorFactory;
    protected final String jobExecutionId;
    protected final JobExecutor jobExecutor;
    protected final ReportEmailCreator reportEmailCreator;
    protected final MailService mailService;
    protected final ParameterCsvExtractor parameterCsvExtractor;
    protected final JobService jobService;
    protected final ReportFailureAlertEmailSender reportFailureAlertEmailSender;

    //Hack to prevent Derby lock error - http://apache-database.10148.n7.nabble.com/Identity-column-and-40XL1-error-tc147382.html
/*    protected static Object sqlQueryExecutionLock = new Object();
    protected static Object jobExecutionLock = new Object();*/

    @Inject
    public Job(JobDao jobDao,
               JobExecutionDao jobExecutionDao,
               SqlQueryDao sqlQueryDao,
               SqlQueryExecutionDao sqlQueryExecutionDao,
               DatasourceService datasourceService,
               ResultSetProcessorFactory resultSetProcessorFactory,
               PreparedStatementExecutorFactory preparedStatementExecutorFactory,
               KweryDirectory kweryDirectory,
               SqlQueryNormalizerFactory sqlQueryNormalizerFactory,
               SqlQueryParameterExtractorFactory sqlQueryParameterExtractorFactory,
               JobExecutor jobExecutor,
               ReportEmailCreator reportEmailCreator,
               MailService mailService,
               ParameterCsvExtractor parameterCsvExtractor,
               JobService jobService,
               ReportFailureAlertEmailSender reportFailureAlertEmailSender,
               @Assisted JobModel jobModel,
               @Assisted Map<String, ?> parameters,
               @Assisted String jobExecutionId) {
        this.jobDao = jobDao;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryDao = sqlQueryDao;
        this.jobModel = jobModel;
        this.parameters = parameters;
        this.jobExecutionId = jobExecutionId;
        this.datasourceService = datasourceService;
        this.preparedStatementExecutorFactory = preparedStatementExecutorFactory;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.resultSetProcessorFactory = resultSetProcessorFactory;
        this.kweryDirectory = kweryDirectory;
        this.sqlQueryNormalizerFactory = sqlQueryNormalizerFactory;
        this.sqlQueryParameterExtractorFactory = sqlQueryParameterExtractorFactory;
        this.jobExecutor = jobExecutor;
        this.reportEmailCreator = reportEmailCreator;
        this.mailService = mailService;
        this.parameterCsvExtractor = parameterCsvExtractor;
        this.jobService = jobService;
        this.reportFailureAlertEmailSender = reportFailureAlertEmailSender;
    }

    @Override
    public JobExecutionModel call() throws Exception {
        try {
            //Save job execution start
            logger.info("Job {} with execution id {} and parameters {} execution started", jobModel.getId(), jobExecutionId, parameters);
            JobExecutionModel jobExecutionModel = saveJobExecutionStart();

            for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
                boolean sqlQueryError = false;
                String sqlExecutionId = UUID.randomUUID().toString();

                SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryModel.getId());
                Datasource datasource = sqlQuery.getDatasource();

                //SQL query execution start
                SqlQueryExecutionModel sqlQueryExecutionModel = saveSqlQueryExecutionStart(jobExecutionModel, sqlQueryModel, sqlExecutionId);

                String query = sqlQuery.getQuery();
                logger.info("Executing query {} on datasource {}", query, datasource.getLabel());
                try (Connection connection = datasourceService.connection(datasource)) {
                    //TODO - Parameterise this
                    if (sqlQuery.getQuery().trim().toLowerCase().startsWith("insert")) {
                        try (PreparedStatement p = connection.prepareStatement(query)) {
                            Future<Integer> queryFuture = preparedStatementExecutorFactory.create(p).executeUpdate();
                            try  {
                                Integer updatedRows = queryFuture.get();
                                logger.info("{} rows updated by query {} running on datasource {}", updatedRows, query, datasource.getLabel());
                            } catch (InterruptedException e) {
                                logger.error("Query {} running on datasource {} cancelled, hence cancelling the prepared statement", query, datasource.getLabel(), e);
                                p.cancel();
                                saveSqlQueryExecutionKilled(sqlQueryExecutionModel.getId());
                                //TODO - Needs investigation
                                //Task executor has been cancelled.
                                //Eat this exception here, if we interrupt the thread as we should, c3p0 connection thread pool gets affected.
                            } catch (ExecutionException e) {
                                sqlQueryError = true;
                                updateFailure(sqlQueryExecutionModel.getId(), e);
                                logger.error("Exception while executing sql query {} with id {} for job {}", query, sqlQuery.getId(), jobModel.getId(), e);
                            }
                        }
                    } else {
                        connection.setAutoCommit(false);

                        //Extract parameters from query if present
                        SqlQueryParameterExtractor extractor = sqlQueryParameterExtractorFactory.create(query);
                        List<String> parametersFromQuery = extractor.extract();

                        //Replace parameters with ?
                        SqlQueryNormalizer sqlQueryNormalizer = sqlQueryNormalizerFactory.create(query);
                        query = sqlQueryNormalizer.normalise();

                        try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                            //Provide arguments to prepared statement
                            int position = 1;
                            for (String parameter : parametersFromQuery) {
                                p.setObject(position, parameters.get(parameter));
                                position = position + 1;
                            }

                            if (sqlQuery.getDatasource().getType() == Datasource.Type.POSTGRESQL
                                    || sqlQuery.getDatasource().getType() == Datasource.Type.REDSHIFT
                                    || sqlQuery.getDatasource().getType() == Datasource.Type.SQLSERVER
                                    ) {
                                //TODO Random value for now, have to tune this
                                p.setFetchSize(100);
                            } else {
                                p.setFetchSize(Integer.MIN_VALUE);
                            }

                            Future<ResultSet> queryFuture = preparedStatementExecutorFactory.create(p).executeSelect();

                            try (ResultSet rs = queryFuture.get()) {
                                File file = kweryDirectory.createFile();
                                resultSetProcessorFactory.create(rs, file).write();
                                SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(sqlExecutionId);
                                sqlQueryExecution.setResultFileName(file.getName());
                                sqlQueryExecutionDao.save(sqlQueryExecution);
                                logger.info("Sql query with id {} and label {} execution completed", sqlQueryModel.getId(), sqlQueryModel.getLabel());
                            } catch (InterruptedException e) {
                                logger.error("Query {} running on datasource {} cancelled, hence cancelling the prepared statement", query, datasource.getLabel(), e);
                                p.cancel();
                                saveSqlQueryExecutionKilled(sqlQueryExecutionModel.getId());
                                //Job got cancelled, get out of the loop
                                break;
                                //TODO - Needs investigation
                                //Task executor has been cancelled.
                                //Eat this exception here, if we interrupt the thread as we should, c3p0 connection thread pool gets affected.
                            } catch (Exception e) {
                                sqlQueryError = true;
                                logger.error("Exception while executing sql query {} with id {} for job {}", query, sqlQuery.getId(), jobModel.getId(), e);
                                if (e instanceof ExecutionException) {
                                    updateFailure(sqlQueryExecutionModel.getId(), (ExecutionException) e);
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    sqlQueryError = true;
                    logger.error("Exception while executing sql query {} with id {} for job {}", query, sqlQuery.getId(), jobModel.getId(), e);
                }

                saveSqlQueryExecutionEnd(sqlQueryExecutionModel.getId(), sqlQueryError);
            }

            jobExecutionModel = saveJobExecutionEnd(jobExecutionModel.getId());

            if (jobExecutionModel.getStatus() == JobExecutionModel.Status.SUCCESS) {
                successActions(jobExecutionModel);
            } else {
                //There are failures or job is killed
                if (!jobExecutionModel.getJobModel().getFailureAlertEmails().isEmpty()) {
                    reportFailureAlertEmailSender.send(jobExecutionModel);
                }
            }

            return jobExecutionModel;
        } catch (Exception e) {
            logger.error("Exception while executing job {} with execution id {}", jobModel.getId(), jobExecutionId, e);
            return null;
        } finally {
            logger.info("Job {} with execution id {} execution ended", jobModel.getId(), jobExecutionId);
            jobExecutor.deregisterJob(getJobExecutionId());
        }
    }

    private void successActions(JobExecutionModel jobExecutionModel) {
        int jobId = jobExecutionModel.getJobModel().getId();

        if ((!jobExecutionModel.getJobModel().getEmails().isEmpty() || parameters.containsKey(ParameterCsvExtractor.JOB_PARAMETER_CSV_EMAIL_HEADER))) {
            KweryMail kweryMail = reportEmailCreator.create(jobExecutionModel, parameterCsvExtractor.emails(parameters));
            if (kweryMail != null) {
                try {
                    mailService.send(kweryMail);
                    logger.info("Job id {} and execution id {} email sent to {}",
                            jobId, jobExecutionModel.getExecutionId(), String.join(", ", kweryMail.getTos()));
                } catch (Exception e) {
                    logger.error("Exception while trying to send report email for job id {} and execution id {}",
                            jobId, jobExecutionModel.getId(), e);
                }
            }
        }

        //Execute dependent jobs
        JobModel job = jobDao.getJobById(jobId);

        for (JobModel dependentJob : job.getChildJobs()) {
            jobService.launch(dependentJob.getId());
        }
    }

    private void saveSqlQueryExecutionEnd(int sqlQueryExecutionId, boolean sqlQueryError) {
        //Otherwise, latest values are not saved
        SqlQueryExecutionModel sqlQueryExecutionModel = sqlQueryExecutionDao.getById(sqlQueryExecutionId);
        if (sqlQueryError) {
            sqlQueryExecutionModel.setStatus(FAILURE);
        } else {
            sqlQueryExecutionModel.setStatus(SUCCESS);
        }

        sqlQueryExecutionModel.setExecutionEnd(System.currentTimeMillis());
        sqlQueryExecutionDao.save(sqlQueryExecutionModel);
    }

    private void saveSqlQueryExecutionKilled(int sqlQueryExecutionId) {
        //Otherwise, latest values are not saved
        SqlQueryExecutionModel sqlQueryExecutionModel = sqlQueryExecutionDao.getById(sqlQueryExecutionId);
        sqlQueryExecutionModel.setStatus(KILLED);
        sqlQueryExecutionModel.setExecutionEnd(System.currentTimeMillis());
        sqlQueryExecutionDao.save(sqlQueryExecutionModel);
    }

    private SqlQueryExecutionModel saveSqlQueryExecutionStart(JobExecutionModel jobExecutionModel, SqlQueryModel sqlQueryModel, String sqlExecutionId) {
        //synchronized (sqlQueryExecutionLock) {
            SqlQueryExecutionModel sqlQueryExecutionModel = new SqlQueryExecutionModel();
            sqlQueryExecutionModel.setExecutionId(sqlExecutionId);
            sqlQueryExecutionModel.setExecutionStart(System.currentTimeMillis());
            sqlQueryExecutionModel.setSqlQuery(sqlQueryModel);
            sqlQueryExecutionModel.setStatus(ONGOING);
            sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
            sqlQueryExecutionDao.save(sqlQueryExecutionModel);
            return sqlQueryExecutionModel;
        //}
    }

    private JobExecutionModel saveJobExecutionEnd(int jobExecutionId) {
        JobExecutionModel jobExecutionModel = jobExecutionDao.getById(jobExecutionId);

        JobExecutionModel.Status status = null;
        for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModel.getSqlQueryExecutionModels()) {
            if (sqlQueryExecutionModel.getStatus() == KILLED) {
                status = JobExecutionModel.Status.KILLED;
                break;
            }
        }

        if (status == null) {
            for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModel.getSqlQueryExecutionModels()) {
                if (sqlQueryExecutionModel.getStatus() == FAILURE) {
                    status = JobExecutionModel.Status.FAILURE;
                    break;
                }
            }
        }

        if (status == null) {
            status = JobExecutionModel.Status.SUCCESS;
        }

        jobExecutionModel.setStatus(status);
        jobExecutionModel.setExecutionEnd(System.currentTimeMillis());
        jobExecutionDao.save(jobExecutionModel);
        return jobExecutionModel;
    }

    private JobExecutionModel saveJobExecutionStart() {
        //synchronized (jobExecutionLock) {
            JobExecutionModel jobExecutionModel = new JobExecutionModel();
            jobExecutionModel.setExecutionId(jobExecutionId);
            jobExecutionModel.setExecutionStart(System.currentTimeMillis());
            jobExecutionModel.setJobModel(jobModel);
            jobExecutionModel.setStatus(JobExecutionModel.Status.ONGOING);
            jobExecutionDao.save(jobExecutionModel);
            return jobExecutionModel;
        //}
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    private void updateFailure(int sqlQueryExecutionId, ExecutionException e) {
        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getById(sqlQueryExecutionId);
        sqlQueryExecution.setExecutionError(e.getCause().getLocalizedMessage());
        sqlQueryExecutionDao.save(sqlQueryExecution);
    }
}
