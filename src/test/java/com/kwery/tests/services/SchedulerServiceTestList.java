package com.kwery.tests.services;

import org.junit.Before;
import com.kwery.services.scheduler.SchedulerService;

public class SchedulerServiceTestList {
    private SchedulerService schedulerService;

    @Before
    public void setUpSchedulerServiceTestList() {
        schedulerService = new SchedulerService();
    }
}
