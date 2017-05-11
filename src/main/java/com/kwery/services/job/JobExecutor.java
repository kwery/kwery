package com.kwery.services.job;

import com.google.inject.Singleton;
import com.kwery.models.JobExecutionModel;
import ninja.lifecycle.Dispose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.*;

@Singleton
public class JobExecutor {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected ExecutorService executorService = Executors.newFixedThreadPool(10);
    protected ConcurrentHashMap<String, Future<JobExecutionModel>> jobExecutionHolder = new ConcurrentHashMap<>();

    public Future<JobExecutionModel> submit(Job job) {
        Future<JobExecutionModel> f = executorService.submit(job);
        jobExecutionHolder.put(job.getJobExecutionId(), f);
        return f;
    }

    public boolean cancel(String jobExecutionId) {
        if (jobExecutionHolder.containsKey(jobExecutionId)) {
            return jobExecutionHolder.get(jobExecutionId).cancel(true);
        }
        return false;
    }

    public Collection<Future<JobExecutionModel>> getSubmittedJobs() {
        return jobExecutionHolder.values();
    }

    public boolean deregisterJob(String jobExecutionId) {
        if (jobExecutionHolder.remove(jobExecutionId) != null) {
            return true;
        }
        return false;
    }

    @Dispose
    public void stop() throws InterruptedException {
        logger.info("Shutting down JobExecutor");
        executorService.shutdown();
        boolean bool = executorService.awaitTermination(2, TimeUnit.MINUTES);
        if (bool) {
            logger.info("JobExecutor shutdown successfully");
        } else {
            logger.info("JobExecutor shutdown forcefully after waiting for 2 minutes");
        }
    }
}
