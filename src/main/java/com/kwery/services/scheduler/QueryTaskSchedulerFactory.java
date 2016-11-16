package com.kwery.services.scheduler;

import it.sauronsoftware.cron4j.TaskExecutor;
import com.kwery.models.SqlQuery;

import java.util.List;

public interface QueryTaskSchedulerFactory {
    SqlQueryTaskScheduler create(List<TaskExecutor> taskExecutors, SqlQuery sqlQuery);
}
