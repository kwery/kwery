package com.kwery.tests.services.job.schedule.dependents;

import com.kwery.models.JobExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class JobServiceScheduleJobWithDependentsFailureTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() {
        jobService.schedule(jobModel.getId());
        waitAtMost(130, SECONDS).until(() -> getJobExecutionModels(JobExecutionModel.Status.SUCCESS).size() >= 2);
        assertThat(getJobExecutionModels(dependentJobModel.getId(), JobExecutionModel.Status.SUCCESS), hasSize(0));
    }

    @Override
    protected String getQuery() {
        return "select * from foo";
    }
}
