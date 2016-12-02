package com.kwery.tests.services.job.launch;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;

public class JobServiceLaunchJobKilledTest extends JobServiceJobSetUpAbstractTest {
    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() ->
                !getJobExecutionModels(JobExecutionModel.Status.ONGOING).isEmpty() && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() == 2));

        JobExecutionModel jobExecutionModel = getJobExecutionModels(JobExecutionModel.Status.ONGOING).get(0);

        jobService.stopExecution(jobExecutionModel.getExecutionId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(JobExecutionModel.Status.KILLED).isEmpty());

        assertJobExecutionModel(JobExecutionModel.Status.KILLED);
        assertSqlQueryExecutionModel(sqlQueryId0, SqlQueryExecutionModel.Status.KILLED);
        assertSqlQueryExecutionModel(sqlQueryId1, SqlQueryExecutionModel.Status.KILLED);
    }

    @Override
    protected String getQuery() {
        return "select sleep(100000)";
    }
}
