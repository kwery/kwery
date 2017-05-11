package com.kwery.services.job;

public interface JobSchedulerTaskFactory {
    JobSchedulerTask create(int jobId);
}
