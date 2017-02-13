package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.JobModel;
import com.kwery.models.JobRuleModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class JobTask extends Task {
    protected final KweryScheduler kweryScheduler;
    protected final JobDao jobDao;
    protected final int jobId;
    protected final SqlQueryTaskFactory sqlQueryTaskFactory;
    protected final JobExecutionDao jobExecutionDao;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;
    protected final JobService jobService;

    @Inject
    public JobTask(KweryScheduler kweryScheduler, JobDao jobDao, JobExecutionDao jobExecutionDao, SqlQueryExecutionDao sqlQueryExecutionDao,
                   SqlQueryTaskFactory sqlQueryTaskFactory, JobService jobService, @Assisted int jobId) {
        this.kweryScheduler = kweryScheduler;
        this.jobDao = jobDao;
        this.sqlQueryTaskFactory = sqlQueryTaskFactory;
        this.jobId = jobId;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.jobService = jobService;
    }

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        JobModel job = jobDao.getJobById(jobId);

        JobRuleModel jobRuleModel = job.getJobRuleModel();

        if (jobRuleModel != null && jobRuleModel.isSequentialSqlQueryExecution()) {
            for (SqlQueryModel sqlQueryModel : job.getSqlQueries()) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SqlQueryTask task = sqlQueryTaskFactory.create(sqlQueryModel.getId(), context.getTaskExecutor().getGuid(), countDownLatch);
                TaskExecutor executor = kweryScheduler.launch(task);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    //In case job gets cancelled
                    if (executor.canBeStopped()) {
                        executor.stop();
                        break;
                    }
                }

                if (jobRuleModel.isStopExecutionOnSqlQueryFailure()) {
                    SqlQueryExecutionModel sqlQueryExecutionModel = sqlQueryExecutionDao.getByExecutionId(executor.getGuid());
                    if (sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.FAILURE) {
                        break;
                    }
                }
            }
        } else {
            int queryCount = job.getSqlQueries().size();

            CountDownLatch countDownLatch = new CountDownLatch(queryCount);

            List<TaskExecutor> executors = new ArrayList<>(queryCount);

            for (SqlQueryModel sqlQueryModel : job.getSqlQueries()) {
                SqlQueryTask task = sqlQueryTaskFactory.create(sqlQueryModel.getId(), context.getTaskExecutor().getGuid(), countDownLatch);
                TaskExecutor executor = kweryScheduler.launch(task);
                executors.add(executor);
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                //In case job gets cancelled
                for (TaskExecutor executor : executors) {
                    if (executor.canBeStopped()) {
                        executor.stop();
                    }
                }
            }
        }

    }

    public int getJobId() {
        return jobId;
    }

    @Override
    public boolean canBeStopped() {
        return true;
    }
}
