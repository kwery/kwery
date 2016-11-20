package com.kwery.services.scheduler;

import it.sauronsoftware.cron4j.TaskExecutor;

public class SqlQueryTaskSchedulerExecutorPair {
    protected SqlQueryTaskScheduler sqlQueryTaskScheduler;
    protected TaskExecutor taskExecutor;

    public SqlQueryTaskSchedulerExecutorPair(SqlQueryTaskScheduler sqlQueryTaskScheduler, TaskExecutor taskExecutor) {
        this.sqlQueryTaskScheduler = sqlQueryTaskScheduler;
        this.taskExecutor = taskExecutor;
    }

    public SqlQueryTaskScheduler getSqlQueryTaskScheduler() {
        return sqlQueryTaskScheduler;
    }

    public void setSqlQueryTaskScheduler(SqlQueryTaskScheduler sqlQueryTaskScheduler) {
        this.sqlQueryTaskScheduler = sqlQueryTaskScheduler;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
