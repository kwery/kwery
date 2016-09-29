package services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.QueryRunExecutionDao;
import it.sauronsoftware.cron4j.TaskExecutor;
import it.sauronsoftware.cron4j.TaskExecutorListener;
import models.QueryRunExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static models.QueryRunExecution.Status.FAILURE;
import static models.QueryRunExecution.Status.KILLED;
import static models.QueryRunExecution.Status.SUCCESS;

@Singleton
public class QueryTaskExecutorListener implements TaskExecutorListener {
    protected Logger logger = LoggerFactory.getLogger(TaskExecutorListener.class);

    protected final QueryRunExecutionDao queryRunExecutionDao;

    @Inject
    public QueryTaskExecutorListener(QueryRunExecutionDao  queryRunExecutionDao) {
        this.queryRunExecutionDao = queryRunExecutionDao;
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

        QueryRunExecution queryRunExecution = queryRunExecutionDao.getByExecutionId(executor.getGuid());

        if (executor.isStopped()) {
            queryRunExecution.setStatus(KILLED);
        } else {
            if (exception != null) {
                queryRunExecution.setStatus(FAILURE);
            } else {
                queryRunExecution.setStatus(SUCCESS);
            }
        }

        queryRunExecution.setExecutionEnd(System.currentTimeMillis());

        queryRunExecutionDao.update(queryRunExecution);
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
