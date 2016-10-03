package services.scheduler;

import models.SqlQuery;

public class OngoingSqlQueryTask {
    private long startTime;
    private String executionId;
    private SqlQuery sqlQuery;

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

    public SqlQuery getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(SqlQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
}
