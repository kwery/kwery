package com.kwery.dtos;

import com.kwery.models.JobLabelModel;

public class JobLabelModelHackDto {
    protected JobLabelModel jobLabelModel;
    protected JobLabelModel parentJobLabelModel;

    public JobLabelModelHackDto(JobLabelModel jobLabelModel, JobLabelModel parentJobLabelModel) {
        this.jobLabelModel = jobLabelModel;
        this.parentJobLabelModel = parentJobLabelModel;
    }

    public JobLabelModel getJobLabelModel() {
        return jobLabelModel;
    }

    public void setJobLabelModel(JobLabelModel jobLabelModel) {
        this.jobLabelModel = jobLabelModel;
    }

    public JobLabelModel getParentJobLabelModel() {
        return parentJobLabelModel;
    }

    public void setParentJobLabelModel(JobLabelModel parentJobLabelModel) {
        this.parentJobLabelModel = parentJobLabelModel;
    }
}
