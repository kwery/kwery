package com.kwery.tests.services.job.launch.dependets;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchWithDependentsKilledJobTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() ->
                !getJobExecutionModels(JobExecutionModel.Status.ONGOING).isEmpty() && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() == 2));

        JobExecutionModel jobExecutionModel = getJobExecutionModels(JobExecutionModel.Status.ONGOING).get(0);

        jobService.stopExecution(jobExecutionModel.getExecutionId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(JobExecutionModel.Status.KILLED).isEmpty());

        assertThat(getJobExecutionModels(dependentJobModel.getId()), hasSize(0));

        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();
        assertThat(mail, nullValue());
    }

    @Override
    protected String getQuery() {
        return "select sleep(1000000)";
    }
}
