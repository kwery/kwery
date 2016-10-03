package services;

import org.junit.Test;
import services.scheduler.SchedulerService;
import services.scheduler.SqlQueryExecutionNotFoundException;

public class SchedulerServiceTestCancelThrowsException {
    @Test(expected = SqlQueryExecutionNotFoundException.class)
    public void test() throws SqlQueryExecutionNotFoundException {
        new SchedulerService().stopExecution(1, "sjdfjklj");
    }
}
