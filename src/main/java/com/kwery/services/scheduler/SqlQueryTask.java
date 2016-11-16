package com.kwery.services.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dao.SqlQueryExecutionDao;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kwery.services.RepoDashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SqlQueryTask extends Task {
    private static Logger logger = LoggerFactory.getLogger(SqlQueryTask.class);

    private SqlQuery sqlQuery;

    private final SqlQueryExecutionDao sqlQueryExecutionDao;
    private final RepoDashUtil repoDashUtil;
    private final ResultSetProcessorFactory resultSetProcessorFactory;
    private final PreparedStatementExecutorFactory preparedStatementExecutorFactory;

    @Inject
    public SqlQueryTask(SqlQueryExecutionDao sqlQueryExecutionDao, RepoDashUtil repoDashUtil, PreparedStatementExecutorFactory preparedStatementExecutorFactory, ResultSetProcessorFactory resultSetProcessorFactory, @Assisted SqlQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
        this.repoDashUtil = repoDashUtil;
        this.preparedStatementExecutorFactory = preparedStatementExecutorFactory;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.resultSetProcessorFactory = resultSetProcessorFactory;
    }

    @Override
    public void execute(TaskExecutionContext context) {
        Datasource datasource = sqlQuery.getDatasource();
        logger.info("Starting query {} on datasource {}", sqlQuery.getQuery(), datasource.getLabel());
        try (Connection connection = repoDashUtil.connection(datasource);
            PreparedStatement p = connection.prepareStatement(sqlQuery.getQuery())) {

            Future<ResultSet> queryFuture = preparedStatementExecutorFactory.create(p).execute();

            try (ResultSet rs = queryFuture.get()) {
                String result = resultSetProcessorFactory.create(rs).process();
                SqlQueryExecution sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(context.getTaskExecutor().getGuid());
                sqlQueryExecution.setResult(result);
                sqlQueryExecutionDao.update(sqlQueryExecution);
            } catch (JsonProcessingException e) {
                logger.error("JSON processing exception while processing result of query {} running on datasource {}", sqlQuery.getQuery(), datasource.getLabel(), e);
            } catch (InterruptedException e) {
                logger.error("Query {} running on datasource {} cancelled, hence cancelling the prepared statement", sqlQuery.getQuery(), datasource.getLabel(), e);
                p.cancel();
            } catch (ExecutionException e) {
                logger.error("Exception while trying to retrieve result set of query {} running on datasource {}", sqlQuery.getQuery(), datasource.getLabel(), e);
            }

        } catch (SQLException e) {
            logger.error("Exception while running query {} on datasource {}", sqlQuery.getQuery(), datasource.getLabel(), e);
        }
    }

    @Override
    public boolean canBeStopped() {
        return true;
    }

    public SqlQuery getSqlQuery() {
        return sqlQuery;
    }
}
