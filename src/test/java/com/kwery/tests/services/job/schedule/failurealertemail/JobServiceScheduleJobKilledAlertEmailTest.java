package com.kwery.tests.services.job.schedule.failurealertemail;

import com.google.common.collect.ImmutableSet;
import com.kwery.tests.services.job.schedule.JobServiceScheduleJobKilledTest;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobFailureAlertEmailDbSetUp;

public class JobServiceScheduleJobKilledAlertEmailTest extends JobServiceScheduleJobKilledTest {
    @Before
    public void setUp() {
        this.disableMailTest();
        jobModel.setFailureAlertEmails(ImmutableSet.of("foo@bar.com"));
        jobFailureAlertEmailDbSetUp(jobModel);
    }

    @Test
    public void test() throws Exception {
        super.test();
        assertReportFailureAlertEmailExists();
    }
}
