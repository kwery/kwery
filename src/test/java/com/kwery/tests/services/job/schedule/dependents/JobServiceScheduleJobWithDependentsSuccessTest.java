package com.kwery.tests.services.job.schedule.dependents;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

public class JobServiceScheduleJobWithDependentsSuccessTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() throws Exception {
        jobService.schedule(jobModel.getId());

        waitAtMost(150, SECONDS).until(() -> getJobExecutionModels(dependentJobModel.getId(), JobExecutionModel.Status.SUCCESS).size() >= 2);

        assertJobExecutionModels(JobExecutionModel.Status.SUCCESS, 2, dependentJobModel.getId());

        assertSqlQueryExecutionModels(sqlQueryId2, SqlQueryExecutionModel.Status.SUCCESS, 2);
        assertSqlQueryExecutionModels(sqlQueryId3, SqlQueryExecutionModel.Status.SUCCESS, 2);

        assertReportEmailExists();
    }

    @Override
    protected String getQuery() {
        return "select 0";
    }
}
