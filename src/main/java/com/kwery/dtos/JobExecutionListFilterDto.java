package com.kwery.dtos;

public class JobExecutionListFilterDto {
    protected int pageNumber;
    protected int resultCount;
    protected String executionStartStart;
    protected String executionStartEnd;

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

    public String getExecutionStartStart() {
        return executionStartStart;
    }

    public void setExecutionStartStart(String executionStartStart) {
        this.executionStartStart = executionStartStart;
    }

    public String getExecutionStartEnd() {
        return executionStartEnd;
    }

    public void setExecutionStartEnd(String executionStartEnd) {
        this.executionStartEnd = executionStartEnd;
    }
}
