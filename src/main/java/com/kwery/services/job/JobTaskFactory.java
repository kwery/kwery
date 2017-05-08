package com.kwery.services.job;

import com.kwery.models.JobModel;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface JobTaskFactory {
    JobTask create(int jobId);
    JobTask create(JobModel jobModel, Map<String, ?> parameters, CountDownLatch jobDone);
}
