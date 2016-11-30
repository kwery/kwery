package com.kwery.services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.SqlQueryExecutionModel;
import it.sauronsoftware.cron4j.TaskExecutor;
import it.sauronsoftware.cron4j.TaskExecutorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.models.SqlQueryExecutionModel.Status.*;

@Singleton
public class SqlQueryTaskExecutorListener implements TaskExecutorListener {
    protected Logger logger = LoggerFactory.getLogger(TaskExecutorListener.class);

    protected final SqlQueryExecutionDao sqlQueryExecutionDao;

    @Inject
    public SqlQueryTaskExecutorListener(SqlQueryExecutionDao sqlQueryExecutionDao) {
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
    }

    @Override
    public void executionPausing(TaskExecutor executor) {
        //Noop
    }

    @Override
    public void executionResuming(TaskExecutor executor) {
        //Noop
    }

    @Override
    public void executionStopping(TaskExecutor executor) {
        //Noop
    }

    @Override
    public void executionTerminated(TaskExecutor executor, Throwable exception) {
        logger.info("{} execution terminated", executor.getGuid());

        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(executor.getGuid());

        if (executor.isStopped()) {
            sqlQueryExecution.setStatus(KILLED);
            logger.info("Execution status - " + KILLED);
        } else {
            if (exception != null) {
                sqlQueryExecution.setStatus(FAILURE);
                logger.info("Execution status - " + FAILURE);
            } else {
                sqlQueryExecution.setStatus(SUCCESS);
                logger.info("Execution status - " + SUCCESS);
            }
        }

        sqlQueryExecution.setExecutionEnd(System.currentTimeMillis());

        sqlQueryExecutionDao.update(sqlQueryExecution);
    }

    @Override
    public void statusMessageChanged(TaskExecutor executor, String statusMessage) {
        //Noop
    }

    @Override
    public void completenessValueChanged(TaskExecutor executor, double completenessValue) {
        //Noop
    }
}
