package com.kwery.services.job;

import java.util.HashSet;
import java.util.Set;

public class JobSearchFilter {
    protected Integer resultCount;
    protected Integer pageNo;
    protected Set<Integer> jobLabelIds = new HashSet<>();
    protected Set<Integer> sqlQueryIds = new HashSet<>();

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Set<Integer> getJobLabelIds() {
        return jobLabelIds;
    }

    public void setJobLabelIds(Set<Integer> jobLabelIds) {
        this.jobLabelIds = jobLabelIds;
    }

    public Set<Integer> getSqlQueryIds() {
        return sqlQueryIds;
    }

    public void setSqlQueryIds(Set<Integer> sqlQueryIds) {
        this.sqlQueryIds = sqlQueryIds;
    }
}
