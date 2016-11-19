package com.kwery.tests.services;

import com.kwery.services.scheduler.OneOffSqlQueryTaskSchedulerReaper;
import com.kwery.services.scheduler.SqlQueryTaskScheduler;
import com.kwery.services.scheduler.SqlQueryTaskSchedulerExecutorPair;
import it.sauronsoftware.cron4j.TaskExecutor;
import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OneOffSqlQueryTaskSchedulerReaperTest {
    @Test
    public void test() {
        OneOffSqlQueryTaskSchedulerReaper reaper = new OneOffSqlQueryTaskSchedulerReaper();

        TaskExecutor aliveExecutor = mock(TaskExecutor.class);
        when(aliveExecutor.isAlive()).thenReturn(true);

        SqlQueryTaskScheduler aliveSqlQueryTaskScheduler = mock(SqlQueryTaskScheduler.class);
        doNothing().when(aliveSqlQueryTaskScheduler).stopScheduler();

        SqlQueryTaskSchedulerExecutorPair alivePair = new SqlQueryTaskSchedulerExecutorPair(aliveSqlQueryTaskScheduler, aliveExecutor);
        reaper.add(alivePair);

        TaskExecutor deadExecutor = mock(TaskExecutor.class);
        when(deadExecutor.isAlive()).thenReturn(false);

        SqlQueryTaskScheduler deadSqlQueryTaskScheduler = mock(SqlQueryTaskScheduler.class);
        doNothing().when(deadSqlQueryTaskScheduler).stopScheduler();

        SqlQueryTaskSchedulerExecutorPair deadPair = new SqlQueryTaskSchedulerExecutorPair(deadSqlQueryTaskScheduler, deadExecutor);
        reaper.add(deadPair);

        assertThat(reaper.getSqlQueryTaskSchedulerExecutorPairs(), hasSize(2));

        reaper.reap();

        assertThat(reaper.getSqlQueryTaskSchedulerExecutorPairs(), hasSize(1));
        assertThat(reaper.getSqlQueryTaskSchedulerExecutorPairs(), containsInAnyOrder(alivePair));
        verify(deadSqlQueryTaskScheduler, times(1)).stopScheduler();
        verify(aliveSqlQueryTaskScheduler, times(0)).stopScheduler();

        reaper.forceReap();

        assertThat(reaper.getSqlQueryTaskSchedulerExecutorPairs(), empty());
        verify(aliveSqlQueryTaskScheduler, times(1)).stopScheduler();
    }
}
