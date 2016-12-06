package com.kwery.services.job;

import com.google.inject.ImplementedBy;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;

@ImplementedBy(KwerySchedulerImpl.class)
public interface KweryScheduler {
    String schedule(String schedulingPattern, Task task);

    TaskExecutor launch(Task task);

    TaskExecutor[] getExecutingTasks();
}
