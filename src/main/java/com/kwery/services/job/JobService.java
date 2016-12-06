package com.kwery.services.job;

import com.google.inject.ImplementedBy;

@ImplementedBy(JobServiceImpl.class)
public interface JobService {
    String schedule(int jobId);

    void launch(int jobId);

    void stopExecution(String executionId);
}
