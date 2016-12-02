package com.kwery.services.job;

import com.kwery.models.JobExecutionModel.Status;

import java.util.List;

public class JobExecutionSearchFilter {
    protected long executionStartStart;
    protected long executionStartEnd;
    protected long executionEndStart;
    protected long executionEndEnd;
    protected List<Status> statuses;
    protected int resultCount;
    protected int pageNumber;
    protected int jobId;
    protected String executionId;

    public long getExecutionStartStart() {
        return executionStartStart;
    }

    public void setExecutionStartStart(long executionStartStart) {
        this.executionStartStart = executionStartStart;
    }

    public long getExecutionStartEnd() {
        return executionStartEnd;
    }

    public void setExecutionStartEnd(long executionStartEnd) {
        this.executionStartEnd = executionStartEnd;
    }

    public long getExecutionEndStart() {
        return executionEndStart;
    }

    public void setExecutionEndStart(long executionEndStart) {
        this.executionEndStart = executionEndStart;
    }

    public long getExecutionEndEnd() {
        return executionEndEnd;
    }

    public void setExecutionEndEnd(long executionEndEnd) {
        this.executionEndEnd = executionEndEnd;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}
