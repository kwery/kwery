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
import com.kwery.models.SqlQueryModel;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.models.SqlQueryExecutionModel.Status.ONGOING;

@Singleton
public class SchedulerListenerImpl implements SchedulerListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final JobDao jobDao;
    protected final JobExecutionDao jobExecutionDao;
    protected final SqlQueryDao sqlQueryDao;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;
    protected final KweryExecutorListener kweryExecutorListener;
    protected final KweryScheduler kweryScheduler;
    protected final JobTaskFactory jobTaskFactory;

    @Inject
    public SchedulerListenerImpl(JobDao jobDao, JobExecutionDao jobExecutionDao, SqlQueryDao sqlQueryDao,
                                 SqlQueryExecutionDao sqlQueryExecutionDao, KweryExecutorListener kweryExecutorListener, KweryScheduler kweryScheduler,
                                 JobTaskFactory jobTaskFactory
    ) {
        this.jobDao = jobDao;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.sqlQueryDao = sqlQueryDao;
        this.kweryExecutorListener = kweryExecutorListener;
        this.kweryScheduler = kweryScheduler;
        this.jobTaskFactory = jobTaskFactory;
    }

    @Override
    public void taskLaunching(TaskExecutor executor) {
        executor.addTaskExecutorListener(kweryExecutorListener);

        Task  task = executor.getTask();
        if (task instanceof SqlQueryTask) {
            SqlQueryTask sqlQueryTask = (SqlQueryTask) task;
            SqlQueryModel sqlQueryModel = sqlQueryDao.getById(sqlQueryTask.getSqlQueryModelId());
            logger.info("Sql query with id {} and label {} execution launched", sqlQueryModel.getId(), sqlQueryModel.getLabel());

            JobExecutionSearchFilter jobExecutionSearchFilter = new JobExecutionSearchFilter();
            jobExecutionSearchFilter.setExecutionId(sqlQueryTask.getJobExecutionId());
            JobExecutionModel jobExecutionModel = jobExecutionDao.filter(jobExecutionSearchFilter).get(0);
            saveSqlQueryExecutionStart(sqlQueryModel, jobExecutionModel, executor);
        } else if (task instanceof JobTask) {
            JobTask jobTask = (JobTask) task;
            JobModel jobModel = jobDao.getJobById(jobTask.getJobId());
            logger.info("Job with id {} and label {} execution launched", jobModel.getId(), jobModel.getLabel());
            saveJobExecutionStart(jobModel, executor);
        } else {
            throw new AssertionError("Unknown task type being executed");
        }
    }

    @Override
    public void taskSucceeded(TaskExecutor executor) {
        Task  task = executor.getTask();
        if (task instanceof SqlQueryTask) {
            SqlQueryTask sqlQueryTask = (SqlQueryTask) task;
            SqlQueryModel sqlQueryModel = sqlQueryDao.getById(sqlQueryTask.getSqlQueryModelId());
            logger.info("Sql query with id {} and label {} execution completed", sqlQueryModel.getId(), sqlQueryModel.getLabel());
        } else if (task instanceof JobTask) {
            JobTask jobTask = (JobTask) task;
            JobModel jobModel = jobDao.getJobById(jobTask.getJobId());
            logger.info("Job with id {} and label {} execution completed", jobModel.getId(), jobModel.getLabel());
        } else {
            throw new AssertionError("Unknown task type being executed");
        }
    }

    @Override
    public void taskFailed(TaskExecutor executor, Throwable exception) {
        Task  task = executor.getTask();
        if (task instanceof SqlQueryTask) {
            SqlQueryTask sqlQueryTask = (SqlQueryTask) task;
            SqlQueryModel sqlQueryModel = sqlQueryDao.getById(sqlQueryTask.getSqlQueryModelId());
            logger.info("Sql query with id {} and label {} execution failed", sqlQueryModel.getId(), sqlQueryModel.getLabel());
        } else if (task instanceof JobTask) {
            JobTask jobTask = (JobTask) task;
            JobModel jobModel = jobDao.getJobById(jobTask.getJobId());
            logger.info("Job with id {} and label {} execution failed", jobModel.getId(), jobModel.getLabel());
        } else {
            throw new AssertionError("Unknown task type being executed");
        }
    }

    protected void saveJobExecutionStart(JobModel jobModel, TaskExecutor taskExecutor) {
        JobExecutionModel e = new JobExecutionModel();
        e.setExecutionId(taskExecutor.getGuid());
        e.setExecutionStart(taskExecutor.getStartTime());
        e.setJobModel(jobDao.getJobById(jobModel.getId()));
        e.setStatus(JobExecutionModel.Status.ONGOING);
        jobExecutionDao.save(e);
    }

    protected void saveSqlQueryExecutionStart(SqlQueryModel sqlQueryModel, JobExecutionModel jobExecutionModel, TaskExecutor taskExecutor) {
        SqlQueryExecutionModel e = new SqlQueryExecutionModel();
        e.setExecutionId(taskExecutor.getGuid());
        e.setExecutionStart(taskExecutor.getStartTime());
        e.setSqlQuery(sqlQueryDao.getById(sqlQueryModel.getId()));
        e.setStatus(ONGOING);
        e.setJobExecutionModel(jobExecutionModel);
        sqlQueryExecutionDao.save(e);
    }
}
