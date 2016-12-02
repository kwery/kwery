package com.kwery.tests.services.job.launch;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;

public class JobServiceLaunchJobFailureTest extends JobServiceJobSetUpAbstractTest {
    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(JobExecutionModel.Status.SUCCESS).isEmpty());

        assertJobExecutionModel(JobExecutionModel.Status.SUCCESS);

        assertSqlQueryExecutionModel(sqlQueryId0, SqlQueryExecutionModel.Status.FAILURE);
        assertSqlQueryExecutionModel(sqlQueryId1, SqlQueryExecutionModel.Status.FAILURE);
    }

    @Override
    protected String getQuery() {
        return "select * from foo";
    }
}
