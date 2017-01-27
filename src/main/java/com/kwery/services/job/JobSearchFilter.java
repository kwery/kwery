package com.kwery.services.job;

import java.util.HashSet;
import java.util.Set;

public class JobSearchFilter {
    protected int resultCount;
    protected int pageNo;
    protected Set<Integer> jobLabelIds = new HashSet<>();

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Set<Integer> getJobLabelIds() {
        return jobLabelIds;
    }

    public void setJobLabelIds(Set<Integer> jobLabelIds) {
        this.jobLabelIds = jobLabelIds;
    }
}
