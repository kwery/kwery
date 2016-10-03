package services.scheduler;

import it.sauronsoftware.cron4j.TaskExecutor;
import models.SqlQuery;

import java.util.List;

public interface QueryTaskSchedulerFactory {
    SqlQueryTaskScheduler create(List<TaskExecutor> taskExecutors, SqlQuery sqlQuery);
}
