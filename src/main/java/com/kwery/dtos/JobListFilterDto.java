package com.kwery.dtos;

public class JobListFilterDto {
    protected int jobLabelId;
    protected int pageNo;
    protected int resultCount;

    public int getJobLabelId() {
        return jobLabelId;
    }

    public void setJobLabelId(int jobLabelId) {
        this.jobLabelId = jobLabelId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
