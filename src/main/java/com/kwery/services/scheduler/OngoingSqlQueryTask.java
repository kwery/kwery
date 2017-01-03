package com.kwery.services.scheduler;

import com.kwery.models.SqlQueryModel;

public class OngoingSqlQueryTask {
    private long startTime;
    private String executionId;
    private SqlQueryModel sqlQuery;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public SqlQueryModel getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(SqlQueryModel sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
}
