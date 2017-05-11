package com.kwery.services.job;

import com.kwery.models.JobModel;

import java.util.Map;

public interface JobFactory {
    Job create(JobModel jobModel, Map<String, ?> parameters, String jobExecutionUuid);
}
