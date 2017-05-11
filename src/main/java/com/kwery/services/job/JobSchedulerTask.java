package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JobSchedulerTask extends Task {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final int jobId;
    protected final JobService jobService;

    @Inject
    public JobSchedulerTask(JobService jobService, @Assisted int jobId) {
        this.jobId = jobId;
        this.jobService = jobService;
    }

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        List<String> executionIds = jobService.launch(jobId);
        logger.info("{} job passed to execution, execution ids {}", jobId, executionIds);
    }
}
