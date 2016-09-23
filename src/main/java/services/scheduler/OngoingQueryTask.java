package services.scheduler;

import models.QueryRun;

public class OngoingQueryTask {
    private long startTime;
    private String executionId;
    private QueryRun queryRun;

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

    public QueryRun getQueryRun() {
        return queryRun;
    }

    public void setQueryRun(QueryRun queryRun) {
        this.queryRun = queryRun;
    }
}
