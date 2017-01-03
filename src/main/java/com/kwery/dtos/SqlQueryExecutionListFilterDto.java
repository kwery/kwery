package com.kwery.dtos;

import java.util.List;

public class SqlQueryExecutionListFilterDto {
    public String executionStartStart;
    public String executionStartEnd;
    public String executionEndStart;
    public String executionEndEnd;
    public List<String> statuses;
    public int pageNumber;
    public int resultCount;

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

    public String getExecutionEndStart() {
        return executionEndStart;
    }

    public void setExecutionEndStart(String executionEndStart) {
        this.executionEndStart = executionEndStart;
    }

    public String getExecutionEndEnd() {
        return executionEndEnd;
    }

    public void setExecutionEndEnd(String executionEndEnd) {
        this.executionEndEnd = executionEndEnd;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
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

    @Override
    public String toString() {
        return "SqlQueryExecutionListFilterDto{" +
                "executionStartStart='" + executionStartStart + '\'' +
                ", executionStartEnd='" + executionStartEnd + '\'' +
                ", executionEndStart='" + executionEndStart + '\'' +
                ", executionEndEnd='" + executionEndEnd + '\'' +
                ", statuses=" + statuses +
                ", pageNumber=" + pageNumber +
                ", resultCount=" + resultCount +
                '}';
    }
}
