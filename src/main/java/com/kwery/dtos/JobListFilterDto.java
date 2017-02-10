package com.kwery.dtos;

public class JobListFilterDto {
    protected int jobLabelId;
    protected int pageNumber;
    protected int resultCount;

    public int getJobLabelId() {
        return jobLabelId;
    }

    public void setJobLabelId(int jobLabelId) {
        this.jobLabelId = jobLabelId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
