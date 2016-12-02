package com.kwery.tests.services.job.schedule;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

public class JobServiceScheduleJobKilledTest extends JobServiceJobSetUpAbstractTest {
    @Test
    public void test() {
        jobService.schedule(jobModel.getId());

        waitAtMost(130, SECONDS).until(() ->
                (getJobExecutionModels(JobExecutionModel.Status.ONGOING).size() >= 2) && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() >= 4));

        List<JobExecutionModel> jobExecutionModels = getJobExecutionModels(JobExecutionModel.Status.ONGOING);

        for (JobExecutionModel jobExecutionModel : jobExecutionModels) {
            jobService.stopExecution(jobExecutionModel.getExecutionId());
        }

        waitAtMost(1, MINUTES).until(() -> getJobExecutionModels(JobExecutionModel.Status.KILLED).size() >= 2);

        assertJobExecutionModels(JobExecutionModel.Status.KILLED, 2);

        assertSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.KILLED, 2);
        assertSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.KILLED, 2);
    }

    @Override
    protected String getQuery() {
        return "select sleep(10000000)";
    }
}
