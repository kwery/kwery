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
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import com.kwery.utils.KweryDirectory;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Inject
    public SqlQueryTask(SqlQueryDao sqlQueryDao,
                        SqlQueryExecutionDao sqlQueryExecutionDao,
                        DatasourceService datasourceService,
                        PreparedStatementExecutorFactory preparedStatementExecutorFactory,
                        ResultSetProcessorFactory resultSetProcessorFactory,
                        KweryDirectory kweryDirectory,
                        @Assisted int sqlQueryModelId,
                        @Assisted String jobExecutionId,
                        @Assisted CountDownLatch latch
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
    }

    @Override
    public void execute(TaskExecutionContext context) {
        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryModelId);
        Datasource datasource = sqlQuery.getDatasource();

        String query = sqlQuery.getQuery();
        logger.info("Executing query {} on datasource {}", query, datasource.getLabel());
        try (Connection connection = datasourceService.connection(datasource);
            PreparedStatement p = connection.prepareStatement(query)) {

            if (sqlQuery.getQuery().trim().toLowerCase().startsWith("insert")) {
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
            } else {
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
