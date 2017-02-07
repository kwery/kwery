package com.kwery.tests.services.job.schedule.dependents;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceScheduleJobWithDependentsJobKilledTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    protected boolean mailTest = true;

    @Test
    public void test() throws Exception {
        jobService.schedule(jobModel.getId());

        waitAtMost(130, SECONDS).until(() ->
                (getJobExecutionModels(JobExecutionModel.Status.ONGOING).size() >= 2) && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() >= 4));

        List<JobExecutionModel> jobExecutionModels = getJobExecutionModels(JobExecutionModel.Status.ONGOING);

        for (JobExecutionModel jobExecutionModel : jobExecutionModels) {
            jobService.stopExecution(jobExecutionModel.getExecutionId());
        }

        waitAtMost(1, MINUTES).until(() -> getJobExecutionModels(JobExecutionModel.Status.KILLED).size() >= 2);

        assertThat(getJobExecutionModels(dependentJobModel.getId(), JobExecutionModel.Status.SUCCESS), hasSize(0));

        if (isMailTest()) {
            assertEmailDoesNotExists();
        }
    }

    @Override
    protected String getQuery() {
        return "select sleep(100000000)";
    }

    protected void disableMailTest() {
        this.mailTest = false;
    }

    public boolean isMailTest() {
        return mailTest;
    }
}
