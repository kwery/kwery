package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import ninja.lifecycle.Dispose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KweryScheduler {
    protected Logger logger = LoggerFactory.getLogger(KweryScheduler.class);

    protected final Scheduler scheduler;

    @Inject
    public KweryScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.scheduler.start();
    }

    public String schedule(String schedulingPattern, Task task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    public void deschedule(String id) {
        scheduler.deschedule(id);
    }

    @Dispose
    public void shutdown() {
        scheduler.stop();
    }
}
