package com.kwery.tests.services.job.launch.dependets;

import com.kwery.models.JobExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobWithDependentsFailureTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() {
        jobService.launch(jobModel.getId());
        waitAtMost(1, MINUTES).until(() -> getJobExecutionModels(JobExecutionModel.Status.FAILURE).size() == 1);
        assertThat(getJobExecutionModels(dependentJobModel.getId()), hasSize(0));

        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();
        assertThat(mail, nullValue());
    }

    @Override
    protected String getQuery() {
        return "select * from foo";
    }
}
