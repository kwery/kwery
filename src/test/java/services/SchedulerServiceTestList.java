package services;

import org.junit.Before;
import services.scheduler.SchedulerService;

public class SchedulerServiceTestList {
    private SchedulerService schedulerService;

    @Before
    public void setUpSchedulerServiceTestList() {
        schedulerService = new SchedulerService();
    }
}
