package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import ninja.lifecycle.Dispose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KweryScheduler {
    protected Logger logger = LoggerFactory.getLogger(KweryScheduler.class);

    protected final Scheduler scheduler;
    protected final SchedulerListener schedulerListener;

    @Inject
    public KweryScheduler(Scheduler scheduler, SchedulerListener listener) {
        this.scheduler = scheduler;
        this.schedulerListener = listener;

        this.scheduler.addSchedulerListener(listener);
        this.scheduler.start();
    }

    public String schedule(String schedulingPattern, Task task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    public void deschedule(String id) {
        scheduler.deschedule(id);
    }

    public TaskExecutor launch(Task task) {
        return scheduler.launch(task);
    }

    public TaskExecutor[] getExecutingTasks() {
        return scheduler.getExecutingTasks();
    }

    @Dispose
    public void shutdown() {
        scheduler.stop();
    }
}
