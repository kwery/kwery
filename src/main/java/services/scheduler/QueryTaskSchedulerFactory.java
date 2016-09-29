package services.scheduler;

import it.sauronsoftware.cron4j.TaskExecutor;
import models.QueryRun;

import java.util.List;

public interface QueryTaskSchedulerFactory {
    QueryTaskScheduler create(List<TaskExecutor> taskExecutors, QueryRun queryRun);
}
