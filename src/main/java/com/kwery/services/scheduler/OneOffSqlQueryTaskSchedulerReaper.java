package com.kwery.services.scheduler;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Singleton;
import ninja.lifecycle.Dispose;
import ninja.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

//TODO - Check whether this might cause memory leak
//TODO - Check performance
@Singleton
public class OneOffSqlQueryTaskSchedulerReaper {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    //TODO - Is this the right DS to use here?
    protected List<SqlQueryTaskSchedulerExecutorPair> sqlQueryTaskSchedulerExecutorPairs = new CopyOnWriteArrayList<>();

    public void add(SqlQueryTaskSchedulerExecutorPair sqlQueryTaskSchedulerExecutorPair) {
        sqlQueryTaskSchedulerExecutorPairs.add(sqlQueryTaskSchedulerExecutorPair);
    }

    @VisibleForTesting
    @Schedule(delay = 2, initialDelay = 2, timeUnit = TimeUnit.MINUTES)
    public void reap() {
        List<SqlQueryTaskSchedulerExecutorPair> toRemove = new LinkedList<>();

        for (SqlQueryTaskSchedulerExecutorPair pair : sqlQueryTaskSchedulerExecutorPairs) {
            if (!pair.getTaskExecutor().isAlive()) {
                pair.getSqlQueryTaskScheduler().stopScheduler();
                toRemove.add(pair);
            }
        }

        logger.info("Reaping {} schedulers", toRemove.size());

        sqlQueryTaskSchedulerExecutorPairs.removeAll(toRemove);
    }

    @VisibleForTesting
    @Dispose
    public void forceReap() {
        for (SqlQueryTaskSchedulerExecutorPair pair : sqlQueryTaskSchedulerExecutorPairs) {
            pair.getSqlQueryTaskScheduler().stopScheduler();
        }

        sqlQueryTaskSchedulerExecutorPairs.clear();
    }

    @VisibleForTesting
    public List<SqlQueryTaskSchedulerExecutorPair> getSqlQueryTaskSchedulerExecutorPairs() {
        return sqlQueryTaskSchedulerExecutorPairs;
    }
}
