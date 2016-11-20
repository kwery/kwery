package com.kwery.tests.services;

import com.kwery.services.scheduler.SqlQueryTaskSchedulerHolder;
import org.junit.Test;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;

public class SchedulerServiceTestCancelThrowsException {
    @Test(expected = SqlQueryExecutionNotFoundException.class)
    public void test() throws SqlQueryExecutionNotFoundException {
        SchedulerService schedulerService = new SchedulerService();
        schedulerService.setSqlQueryTaskSchedulerHolder(new SqlQueryTaskSchedulerHolder());
        schedulerService.stopExecution(1, "sjdfjklj");
    }
}
