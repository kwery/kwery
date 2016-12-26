package com.kwery.services.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.RepoDashUtil;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final RepoDashUtil repoDashUtil;
    private final ResultSetProcessorFactory resultSetProcessorFactory;
    private final PreparedStatementExecutorFactory preparedStatementExecutorFactory;
    private final CountDownLatch latch;
    private final String jobExecutionId;

    @Inject
    public SqlQueryTask(SqlQueryDao sqlQueryDao,
                        SqlQueryExecutionDao sqlQueryExecutionDao,
                        RepoDashUtil repoDashUtil,
                        PreparedStatementExecutorFactory preparedStatementExecutorFactory,
                        ResultSetProcessorFactory resultSetProcessorFactory,
                        @Assisted int sqlQueryModelId,
                        @Assisted String jobExecutionId,
                        @Assisted CountDownLatch latch
    ) {
        this.sqlQueryDao = sqlQueryDao;
        this.repoDashUtil = repoDashUtil;
        this.preparedStatementExecutorFactory = preparedStatementExecutorFactory;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.resultSetProcessorFactory = resultSetProcessorFactory;
        this.sqlQueryModelId = sqlQueryModelId;
        this.latch = latch;
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public void execute(TaskExecutionContext context) {
        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryModelId);
        Datasource datasource = sqlQuery.getDatasource();

        logger.info("Executing query {} on datasource {}", sqlQuery.getQuery(), datasource.getLabel());
        try (Connection connection = repoDashUtil.connection(datasource);
            PreparedStatement p = connection.prepareStatement(sqlQuery.getQuery())) {

            Future<ResultSet> queryFuture = preparedStatementExecutorFactory.create(p).execute();

            try (ResultSet rs = queryFuture.get()) {
                String result = resultSetProcessorFactory.create(rs).process();
                SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(context.getTaskExecutor().getGuid());
                sqlQueryExecution.setResult(result);
                sqlQueryExecutionDao.save(sqlQueryExecution);
            } catch (JsonProcessingException e) {
                logger.error("JSON processing exception while processing result of query {} running on datasource {}", sqlQuery.getQuery(), datasource.getLabel(), e);
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                logger.error("Query {} running on datasource {} cancelled, hence cancelling the prepared statement", sqlQuery.getQuery(), datasource.getLabel(), e);
                p.cancel();
                //TODO - Needs investigation
                //Task executor has been cancelled.
                //Eat this exception here, if we interrupt the thread as we should, c3p0 connection thread pool gets affected.
            } catch (ExecutionException e) {
                logger.error("Exception while trying to retrieve result set of query {} running on datasource {}", sqlQuery.getQuery(), datasource.getLabel(), e);
                SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(context.getTaskExecutor().getGuid());
                sqlQueryExecution.setResult(e.getCause().getLocalizedMessage());
                sqlQueryExecutionDao.save(sqlQueryExecution);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Exception while running query {} on datasource {}", sqlQuery.getQuery(), datasource.getLabel(), e);
            throw new RuntimeException(e);
        }
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
