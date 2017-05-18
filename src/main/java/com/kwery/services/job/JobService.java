package com.kwery.services.job;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.services.job.parameterised.ParameterCsvExtractor;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class JobService {
    protected Logger logger = LoggerFactory.getLogger(JobService.class);

    protected final KweryScheduler kweryScheduler;
    protected final JobDao jobDao;
    protected final JobSchedulerTaskFactory jobSchedulerTaskFactory;
    //TODO - Needs to be done in a better way
    protected Map<Integer, String> jobIdSchedulerIdMap = new ConcurrentHashMap<>();
    protected final ParameterCsvExtractor parameterCsvExtractor;
    protected final JobFactory jobFactory;
    protected final JobExecutor jobExecutor;

    @Inject
    public JobService(KweryScheduler kweryScheduler,
                      JobSchedulerTaskFactory jobSchedulerTaskFactory,
                      ParameterCsvExtractor parameterCsvExtractor,
                      JobFactory jobFactory,
                      JobExecutor jobExecutor,
                      JobDao jobDao) {
        this.kweryScheduler = kweryScheduler;
        this.jobDao = jobDao;
        this.jobSchedulerTaskFactory = jobSchedulerTaskFactory;
        this.parameterCsvExtractor = parameterCsvExtractor;
        this.jobFactory = jobFactory;
        this.jobExecutor = jobExecutor;
    }

    public String schedule(int jobId) {
        logger.info("Scheduling job with id {}", jobId);
        JobModel jobModel = jobDao.getJobById(jobId);
        String scheduleId = kweryScheduler.schedule(jobModel.getCronExpression(), jobSchedulerTaskFactory.create(jobId));
        jobIdSchedulerIdMap.put(jobId, scheduleId);
        return scheduleId;
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

    public List<String> launch(int jobId) {
        JobModel jobModel = jobDao.getJobById(jobId);

        List<Map<String, ?>> parameters = null;
        try {
            parameters = parameterCsvExtractor.extract(jobModel.getParameterCsv());
        } catch (IOException e) {
            logger.error("Exception while extracting parameters from parameter CSV", e);
            throw new RuntimeException(e);
        }

        List<String> executionIds = new LinkedList<>();

        if (!parameters.isEmpty()) {
            for (Map<String, ?> parameter : parameters) {
                String jobExecutionUuid = UUID.randomUUID().toString();
                logger.info("Job {} with execution id {} submitted for execution", jobId, jobExecutionUuid);
                executionIds.add(jobExecutionUuid);
                Job job = jobFactory.create(jobModel, parameter, jobExecutionUuid);
                jobExecutor.submit(job);
            }
        } else {
            String jobExecutionUuid = UUID.randomUUID().toString();
            executionIds.add(jobExecutionUuid);
            Job job = jobFactory.create(jobModel, new HashMap<>(), jobExecutionUuid);
            logger.info("Job {} with execution id {} submitted for execution", jobId, jobExecutionUuid);
            jobExecutor.submit(job);
        }

        return executionIds;
    }

    public boolean stopExecution(String executionId) {
        logger.info("Trying to stop task execution with id {}", executionId);
        return jobExecutor.cancel(executionId);
    }

    public void deschedule(int jobId) {
        logger.info("Deleting job with id {}", jobId);

        String scheduleId = jobIdSchedulerIdMap.get(jobId);

        if (scheduleId == null) {
            throw new RuntimeException("Schedule id not found for job id " + jobId);
        }

        jobIdSchedulerIdMap.remove(jobId);
        kweryScheduler.deschedule(scheduleId);
    }

    @VisibleForTesting
    public Map<Integer, String> getJobIdSchedulerIdMap() {
        return jobIdSchedulerIdMap;
    }
}
