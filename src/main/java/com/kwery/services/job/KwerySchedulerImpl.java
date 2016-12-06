package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;

@Singleton
public class KwerySchedulerImpl implements KweryScheduler {
    protected final Scheduler scheduler;
    protected final SchedulerListener schedulerListener;

    @Inject
    public KwerySchedulerImpl(Scheduler scheduler, SchedulerListener listener) {
        this.scheduler = scheduler;
        this.schedulerListener = listener;

        this.scheduler.addSchedulerListener(listener);
        this.scheduler.start();
    }

    @Override
    public String schedule(String schedulingPattern, Task task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    @Override
    public TaskExecutor launch(Task task) {
        return scheduler.launch(task);
    }

    @Override
    public TaskExecutor[] getExecutingTasks() {
        return scheduler.getExecutingTasks();
    }
}
