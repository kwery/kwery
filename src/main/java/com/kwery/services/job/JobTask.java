package com.kwery.services.job;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class JobTask extends Task {
    protected final KweryScheduler kweryScheduler;
    protected final JobDao jobDao;
    protected int jobId;
    protected final SqlQueryTaskFactory sqlQueryTaskFactory;
    protected final JobExecutionDao jobExecutionDao;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;
    protected final JobService jobService;
    protected JobModel jobModel;
    //This has been introduced to co ordinate parameterised query executions
    protected CountDownLatch jobDone;
    protected Map<String, ?> parameters = new HashMap<>();

    @AssistedInject
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

    @AssistedInject
    public JobTask(KweryScheduler kweryScheduler, JobDao jobDao, JobExecutionDao jobExecutionDao, SqlQueryExecutionDao sqlQueryExecutionDao,
                   SqlQueryTaskFactory sqlQueryTaskFactory, JobService jobService, @Assisted JobModel jobModel, @Assisted Map<String, ?> parameters, @Assisted CountDownLatch jobDone) {
        this.kweryScheduler = kweryScheduler;
        this.jobDao = jobDao;
        this.sqlQueryTaskFactory = sqlQueryTaskFactory;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.jobService = jobService;
        this.jobModel = jobModel;
        this.jobDone = jobDone;
        this.parameters = parameters;
    }

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        try {
            if (jobModel == null) {
                jobModel = jobDao.getJobById(jobId);
            }

            JobRuleModel jobRuleModel = jobModel.getJobRuleModel();

            if (jobRuleModel != null && jobRuleModel.isSequentialSqlQueryExecution()) {
                for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    SqlQueryTask task = sqlQueryTaskFactory.create(sqlQueryModel.getId(), context.getTaskExecutor().getGuid(), countDownLatch, parameters);
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
                int queryCount = jobModel.getSqlQueries().size();

                CountDownLatch countDownLatch = new CountDownLatch(queryCount);

                List<TaskExecutor> executors = new ArrayList<>(queryCount);

                for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
                    SqlQueryTask task = sqlQueryTaskFactory.create(sqlQueryModel.getId(), context.getTaskExecutor().getGuid(), countDownLatch, parameters);
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
        } finally {
            if (this.jobDone != null) {
                this.jobDone.countDown();
            }
        }
    }

    public int getJobId() {
        return jobId;
    }

    public JobModel getJobModel() {
        return jobModel;
    }

    @Override
    public boolean canBeStopped() {
        return true;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }
}
