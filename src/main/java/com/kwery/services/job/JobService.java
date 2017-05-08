package com.kwery.services.job;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.services.job.parameterised.ParameterisedJobTask;
import com.kwery.services.job.parameterised.ParameterisedJobTaskFactory;
import it.sauronsoftware.cron4j.TaskExecutor;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class JobService {
    protected Logger logger = LoggerFactory.getLogger(JobService.class);

    protected final KweryScheduler kweryScheduler;
    protected final JobTaskFactory jobTaskFactory;
    protected final JobDao jobDao;
    protected final ParameterisedKweryScheduler parameterisedKweryScheduler;
    protected final ParameterisedJobTaskFactory parameterisedJobTaskFactory;

    //TODO - Needs to be done in a better way
    protected Map<Integer, String> jobIdSchedulerIdMap = new ConcurrentHashMap<>();

    @Inject
    public JobService(KweryScheduler kweryScheduler, ParameterisedKweryScheduler parameterisedKweryScheduler,
                      JobTaskFactory jobTaskFactory, ParameterisedJobTaskFactory parameterisedJobTaskFactory, JobDao jobDao) {
        this.kweryScheduler = kweryScheduler;
        this.parameterisedKweryScheduler = parameterisedKweryScheduler;
        this.jobTaskFactory = jobTaskFactory;
        this.jobDao = jobDao;
        this.parameterisedJobTaskFactory = parameterisedJobTaskFactory;
    }

    public String schedule(int jobId) {
        logger.info("Scheduling job with id {}", jobId);
        JobModel jobModel = jobDao.getJobById(jobId);

        String schedulerId = "";

        if (jobModel.isParameterised()) {
            ParameterisedJobTask jobTask = parameterisedJobTaskFactory.create(jobId);
            schedulerId = parameterisedKweryScheduler.schedule(jobModel.getCronExpression(), jobTask);
            jobIdSchedulerIdMap.put(jobId, schedulerId);
        } else {
            JobTask jobTask = jobTaskFactory.create(jobId);
            schedulerId = kweryScheduler.schedule(jobModel.getCronExpression(), jobTask);
            jobIdSchedulerIdMap.put(jobId, schedulerId);
        }

        return schedulerId;
    }

    @Start
    public void scheduleAllJobs() {
        List<JobModel> jobModels = jobDao.getAllJobs();
        for (JobModel jobModel : jobModels) {
            if (!"".equals(Strings.nullToEmpty(jobModel.getCronExpression()))) {
                schedule(jobModel.getId());
            }
        }
    }

    public TaskExecutor launch(int jobId) {
        logger.info("Launching job with id {}", jobId);
        if (jobDao.getJobById(jobId).isParameterised()) {
            return parameterisedKweryScheduler.launch(parameterisedJobTaskFactory.create(jobId));
        } else {
            return kweryScheduler.launch(jobTaskFactory.create(jobId));
        }
    }

    public boolean stopExecution(String executionId) {
        logger.info("Trying to stop task execution with id {}", executionId);

        boolean found = false;

        for (TaskExecutor taskExecutor : kweryScheduler.getExecutingTasks()) {
            if (executionId.equals(taskExecutor.getGuid())) {
                taskExecutor.stop();
                found = true;
                logger.info("Task execution with id {} stopped successfully", executionId);
                break;
            }
        }

        if (!found) {
            for (TaskExecutor taskExecutor : parameterisedKweryScheduler.getExecutingTasks()) {
                if (executionId.equals(taskExecutor.getGuid())) {
                    taskExecutor.stop();
                    found = true;
                    logger.info("Task execution with id {} stopped successfully", executionId);
                    break;
                }
            }
        }

        if (!found) {
            logger.info("Task execution with id {} not found", executionId);
        }

        return found;
    }

    public void deschedule(int jobId) {
        logger.info("Deleting job with id {}", jobId);
        String id = jobIdSchedulerIdMap.get(jobId);

        if (id == null) {
            throw new RuntimeException("Schedule id not found for job id " + jobId);
        }

        jobIdSchedulerIdMap.remove(jobId);
        kweryScheduler.deschedule(id);
        parameterisedKweryScheduler.deschedule(id);
    }

    @VisibleForTesting
    public Map<Integer, String> getJobIdSchedulerIdMap() {
        return jobIdSchedulerIdMap;
    }
}
