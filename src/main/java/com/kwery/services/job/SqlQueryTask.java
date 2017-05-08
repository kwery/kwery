package com.kwery.services.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.services.job.parameterised.SqlQueryNormalizer;
import com.kwery.services.job.parameterised.SqlQueryNormalizerFactory;
import com.kwery.services.job.parameterised.SqlQueryParameterExtractor;
import com.kwery.services.job.parameterised.SqlQueryParameterExtractorFactory;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import com.kwery.conf.KweryDirectory;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SqlQueryTask extends Task {
    private static Logger logger = LoggerFactory.getLogger(SqlQueryTask.class);

    private final int sqlQueryModelId;

    private final SqlQueryDao sqlQueryDao;
    private final SqlQueryExecutionDao sqlQueryExecutionDao;
    private final DatasourceService datasourceService;
    private final ResultSetProcessorFactory resultSetProcessorFactory;
    private final PreparedStatementExecutorFactory preparedStatementExecutorFactory;
    private final CountDownLatch latch;
    private final String jobExecutionId;
    protected final KweryDirectory kweryDirectory;
    protected final Map<String, ?> parameters;
    protected final SqlQueryNormalizerFactory sqlQueryNormalizerFactory;
    protected final SqlQueryParameterExtractorFactory sqlQueryParameterExtractorFactory;


    @Inject
    public SqlQueryTask(SqlQueryDao sqlQueryDao,
                        SqlQueryExecutionDao sqlQueryExecutionDao,
                        DatasourceService datasourceService,
                        PreparedStatementExecutorFactory preparedStatementExecutorFactory,
                        ResultSetProcessorFactory resultSetProcessorFactory,
                        KweryDirectory kweryDirectory,
                        SqlQueryNormalizerFactory sqlQueryNormalizerFactory,
                        SqlQueryParameterExtractorFactory sqlQueryParameterExtractorFactory,
                        @Assisted int sqlQueryModelId,
                        @Assisted String jobExecutionId,
                        @Assisted CountDownLatch latch,
                        @Assisted Map<String, ?> parameters
    ) {
        this.sqlQueryDao = sqlQueryDao;
        this.datasourceService = datasourceService;
        this.preparedStatementExecutorFactory = preparedStatementExecutorFactory;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.resultSetProcessorFactory = resultSetProcessorFactory;
        this.sqlQueryModelId = sqlQueryModelId;
        this.latch = latch;
        this.jobExecutionId = jobExecutionId;
        this.kweryDirectory = kweryDirectory;
        this.parameters = parameters;
        this.sqlQueryNormalizerFactory = sqlQueryNormalizerFactory;
        this.sqlQueryParameterExtractorFactory = sqlQueryParameterExtractorFactory;
    }

    @Override
    public void execute(TaskExecutionContext context) {
        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryModelId);
        Datasource datasource = sqlQuery.getDatasource();

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
                        //TODO - Needs investigation
                        //Task executor has been cancelled.
                        //Eat this exception here, if we interrupt the thread as we should, c3p0 connection thread pool gets affected.
                    } catch (ExecutionException e) {
                        logger.error("Exception while trying to retrieve result set of query {} running on datasource {}", query, datasource.getLabel(), e);
                        updateFailure(context, e);
                        throw new RuntimeException(e);
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
                        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(context.getTaskExecutor().getGuid());
                        sqlQueryExecution.setResultFileName(file.getName());
                        sqlQueryExecutionDao.save(sqlQueryExecution);
                    } catch (JsonProcessingException e) {
                        logger.error("JSON processing exception while processing result of query {} running on datasource {}", query, datasource.getLabel(), e);
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        logger.error("Query {} running on datasource {} cancelled, hence cancelling the prepared statement", query, datasource.getLabel(), e);
                        p.cancel();
                        //TODO - Needs investigation
                        //Task executor has been cancelled.
                        //Eat this exception here, if we interrupt the thread as we should, c3p0 connection thread pool gets affected.
                    } catch (ExecutionException e) {
                        logger.error("Exception while trying to retrieve result set of query {} running on datasource {}", query, datasource.getLabel(), e);
                        updateFailure(context, e);
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        logger.error("Exception while trying to write result set of query {} running on datasource {} to file", query, datasource.getLabel(), e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Exception while running query {} on datasource {}", query, datasource.getLabel(), e);
            throw new RuntimeException(e);
        }
    }

    private void updateFailure(TaskExecutionContext context, ExecutionException e) {
        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(context.getTaskExecutor().getGuid());
        sqlQueryExecution.setExecutionError(e.getCause().getLocalizedMessage());
        sqlQueryExecutionDao.save(sqlQueryExecution);
    }

    public void countdown() {
        this.latch.countDown();
    }

    @Override
    public boolean canBeStopped() {
        return true;
    }

    public int getSqlQueryModelId() {
        return sqlQueryModelId;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }
}
