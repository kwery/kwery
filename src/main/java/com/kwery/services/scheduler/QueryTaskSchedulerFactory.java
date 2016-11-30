package com.kwery.services.scheduler;

import com.kwery.models.SqlQueryModel;
import it.sauronsoftware.cron4j.TaskExecutor;

import java.util.List;

public interface QueryTaskSchedulerFactory {
    SqlQueryTaskScheduler create(List<TaskExecutor> taskExecutors, SqlQueryModel sqlQuery);
}
