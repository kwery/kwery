package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import it.sauronsoftware.cron4j.TaskExecutorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.models.JobExecutionModel.Status.*;

@Singleton
public class KweryExecutorListener implements TaskExecutorListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final JobDao jobDao;
    protected final JobExecutionDao jobExecutionDao;
    protected final SqlQueryDao sqlQueryDao;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;

    @Inject
    public KweryExecutorListener(JobDao jobDao, JobExecutionDao jobExecutionDao, SqlQueryDao sqlQueryDao, SqlQueryExecutionDao sqlQueryExecutionDao) {
        this.jobDao = jobDao;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryDao = sqlQueryDao;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
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
                    jobExecutionModel.setStatus(SUCCESS);
                }
            }

            logger.info("Status of job {} is {}", jobTask.getJobId(), jobExecutionModel.getStatus());

            jobExecutionModel.setExecutionEnd(System.currentTimeMillis());
            jobExecutionDao.save(jobExecutionModel);
        } else if (task instanceof SqlQueryTask) {
            SqlQueryTask sqlQueryTask = (SqlQueryTask) task;

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

            model.setExecutionEnd(System.currentTimeMillis());
            sqlQueryExecutionDao.save(model);
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
}
