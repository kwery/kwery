package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.job.parameterised.ParameterCsvExtractor;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import it.sauronsoftware.cron4j.TaskExecutorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.models.JobExecutionModel.Status.*;

@Singleton
public class TaskExecutorListenerImpl implements TaskExecutorListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final JobDao jobDao;
    protected final JobExecutionDao jobExecutionDao;
    protected final SqlQueryDao sqlQueryDao;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;
    protected final JobService jobService;
    protected final ReportEmailCreator reportEmailCreator;
    protected final ReportFailureAlertEmailSender reportFailureAlertEmailSender;
    protected final ParameterCsvExtractor parameterCsvExtractor;
    protected final MailService mailService;

    @Inject
    public TaskExecutorListenerImpl(JobDao jobDao,
                                    JobExecutionDao jobExecutionDao,
                                    SqlQueryDao sqlQueryDao,
                                    SqlQueryExecutionDao sqlQueryExecutionDao,
                                    JobService jobService,
                                    ReportEmailCreator reportEmailCreator,
                                    MailService mailService,
                                    ReportFailureAlertEmailSender reportFailureAlertEmailSender,
                                    ParameterCsvExtractor parameterCsvExtractor
                                    ) {
        this.jobDao = jobDao;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryDao = sqlQueryDao;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.jobService = jobService;
        this.reportEmailCreator = reportEmailCreator;
        this.reportFailureAlertEmailSender = reportFailureAlertEmailSender;
        this.parameterCsvExtractor = parameterCsvExtractor;
        this.mailService = mailService;
    }

    @Override
    public void executionPausing(TaskExecutor executor) {
    }

    @Override
    public void executionResuming(TaskExecutor executor) {
    }

    @Override
    public void executionStopping(TaskExecutor executor) {
    }

    @Override
    public void executionTerminated(TaskExecutor executor, Throwable exception) {
        Task task = executor.getTask();

        if (task instanceof JobTask) {
            JobTask jobTask = (JobTask) task;

            JobExecutionModel jobExecutionModel = jobExecutionDao.getByExecutionId(executor.getGuid());

            if (executor.isStopped()) {
                jobExecutionModel.setStatus(KILLED);
            } else {
                if (exception != null) {
                   jobExecutionModel.setStatus(FAILURE);
                } else {
                    if (areThereKilledOrFailedSqlQueries(jobExecutionModel)) {
                        jobExecutionModel.setStatus(FAILURE);
                    } else {
                        jobExecutionModel.setStatus(SUCCESS);
                    }
                }
            }

            int jobId = jobTask.getJobId();

            if (jobId == 0) {
                jobId = jobTask.getJobModel().getId();
            }

            logger.info("Status of job {} is {}", jobId, jobExecutionModel.getStatus());
            if (exception != null) {
                logger.error("Job {} failed", jobId, exception);
            }

            jobExecutionModel.setExecutionEnd(System.currentTimeMillis());
            jobExecutionModel = jobExecutionDao.save(jobExecutionModel);

            //Should be called only in case of successful Job execution
            if (!executor.isStopped() && exception == null) {
                //Send email
                if ((!jobExecutionModel.getJobModel().getEmails().isEmpty() || jobTask.getParameters().containsKey(ParameterCsvExtractor.JOB_PARAMETER_CSV_EMAIL_HEADER))
                        && hasSqlQueriesExecutedSuccessfully(jobExecutionModel)) {
                    KweryMail kweryMail = reportEmailCreator.create(jobExecutionModel, parameterCsvExtractor.emails(jobTask.getParameters()));
                    if (kweryMail != null) {
                        try {
                            mailService.send(kweryMail);
                            logger.info("Job id {} and execution id {} email sent to {}", jobId, jobExecutionModel.getId(), String.join(", ", kweryMail.getTos()));
                        } catch (Exception e) {
                            logger.error("Exception while trying to send report email for job id {} and execution id {}", jobId, jobExecutionModel.getId(), e);
                        }
                    }
                }

                //Execute dependent jobs
                JobModel job = jobDao.getJobById(jobId);

                if (!job.getChildJobs().isEmpty()) {
                    if (hasSqlQueriesExecutedSuccessfully(jobExecutionModel)) {
                        for (JobModel dependentJob : job.getChildJobs()) {
                            jobService.launch(dependentJob.getId());
                        }
                    }
                }
            }

            //There are failures or job is killed
            if (!jobExecutionModel.getJobModel().getFailureAlertEmails().isEmpty()
                    && (executor.isStopped() || !hasSqlQueriesExecutedSuccessfully(jobExecutionModel))) {
                reportFailureAlertEmailSender.send(jobExecutionModel);
            }
        } else if (task instanceof SqlQueryTask) {
            SqlQueryTask sqlQueryTask = null;
            try {
                sqlQueryTask = (SqlQueryTask) task;

                SqlQueryExecutionModel model = sqlQueryExecutionDao.getByExecutionId(executor.getGuid());

                if (executor.isStopped()) {
                    model.setStatus(SqlQueryExecutionModel.Status.KILLED);
                } else {
                    if (exception != null) {
                        model.setStatus(SqlQueryExecutionModel.Status.FAILURE);
                    } else {
                        model.setStatus(SqlQueryExecutionModel.Status.SUCCESS);
                    }
                }

                logger.info("Status of sql query id {} execution is {}", sqlQueryTask.getSqlQueryModelId(), model.getStatus());
                if (exception != null) {
                    logger.error("Sql query with id {} execution failed", sqlQueryTask.getSqlQueryModelId(), exception);
                }

                model.setExecutionEnd(System.currentTimeMillis());
                sqlQueryExecutionDao.save(model);

            } finally {
                sqlQueryTask.countdown();
            }
        } else {
            throw new AssertionError("Unknown task type being terminated");
        }
    }

    @Override
    public void statusMessageChanged(TaskExecutor executor, String statusMessage) {
    }

    @Override
    public void completenessValueChanged(TaskExecutor executor, double completenessValue) {
    }

    private boolean hasSqlQueriesExecutedSuccessfully(JobExecutionModel jobExecutionModel) {
        boolean success = true;
        for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModel.getSqlQueryExecutionModels()) {
            success = success && (sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.SUCCESS);
        }
        return success;
    }

    private boolean areThereKilledOrFailedSqlQueries(JobExecutionModel jobExecutionModel) {
        for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModel.getSqlQueryExecutionModels()) {
            if (sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.FAILURE
                    || sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.KILLED) {
                return true;
            }
        }

        return false;
    }
}
