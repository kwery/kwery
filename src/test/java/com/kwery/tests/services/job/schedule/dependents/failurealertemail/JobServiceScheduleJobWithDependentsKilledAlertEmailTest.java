package com.kwery.tests.services.job.schedule.dependents.failurealertemail;

import com.google.common.collect.ImmutableSet;
import com.kwery.tests.services.job.schedule.dependents.JobServiceScheduleJobWithDependentsJobKilledTest;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobFailureAlertEmailDbSetUp;

public class JobServiceScheduleJobWithDependentsKilledAlertEmailTest extends JobServiceScheduleJobWithDependentsJobKilledTest {
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
