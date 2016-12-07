package com.kwery.tests.services.job.launch.dependets;

import com.kwery.models.JobExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobWithDependentsFailureTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() {
        jobService.launch(jobModel.getId());
        waitAtMost(1, MINUTES).until(() -> getJobExecutionModels(JobExecutionModel.Status.FAILURE).size() == 1);
        assertThat(getJobExecutionModels(dependentJobModel.getId()), hasSize(0));
    }

    @Override
    protected String getQuery() {
        return "select * from foo";
    }
}
