package com.kwery.services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.SqlQueryExecutionDao;
import it.sauronsoftware.cron4j.TaskExecutor;
import it.sauronsoftware.cron4j.TaskExecutorListener;
import com.kwery.models.SqlQueryExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.models.SqlQueryExecution.Status.FAILURE;
import static com.kwery.models.SqlQueryExecution.Status.KILLED;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;

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

        SqlQueryExecution sqlQueryExecution = sqlQueryExecutionDao.getByExecutionId(executor.getGuid());

        if (executor.isStopped()) {
            sqlQueryExecution.setStatus(KILLED);
        } else {
            if (exception != null) {
                sqlQueryExecution.setStatus(FAILURE);
            } else {
                sqlQueryExecution.setStatus(SUCCESS);
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
