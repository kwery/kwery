package com.kwery.tests.services;

import org.junit.Test;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;

public class SchedulerServiceTestCancelThrowsException {
    @Test(expected = SqlQueryExecutionNotFoundException.class)
    public void test() throws SqlQueryExecutionNotFoundException {
        new SchedulerService().stopExecution(1, "sjdfjklj");
    }
}
