package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import it.sauronsoftware.cron4j.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JobServiceImpl implements JobService {
    protected Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    protected final KweryScheduler kweryScheduler;
    protected final JobTaskFactory jobTaskFactory;
    protected final JobDao jobDao;

    @Inject
    public JobServiceImpl(KweryScheduler kweryScheduler, JobTaskFactory jobTaskFactory, JobDao jobDao) {
        this.kweryScheduler = kweryScheduler;
        this.jobTaskFactory = jobTaskFactory;
        this.jobDao = jobDao;
    }

    @Override
    public String schedule(int jobId) {
        logger.info("Scheduling job with id {}", jobId);
        JobTask jobTask = jobTaskFactory.create(jobId);
        JobModel jobModel = jobDao.getJobById(jobId);
        return kweryScheduler.schedule(jobModel.getCronExpression(), jobTask);
    }

    @Override
    public void launch(int jobId) {
        logger.info("Launching job with id {}", jobId);
        kweryScheduler.launch(jobTaskFactory.create(jobId));
    }

    @Override
    public void stopExecution(String executionId) {
        logger.info("Trying to stop task execution with id {}", executionId);

        boolean found = false;

        for (TaskExecutor taskExecutor : kweryScheduler.getExecutingTasks()) {
            if (executionId.equals(taskExecutor.getGuid())) {
                taskExecutor.stop();
                found = true;
                logger.info("Task execution with id {} stopped successfully", executionId);
            }
        }

        if (!found) {
            logger.info("Task execution with id {} not found", executionId);
        }
    }
}
