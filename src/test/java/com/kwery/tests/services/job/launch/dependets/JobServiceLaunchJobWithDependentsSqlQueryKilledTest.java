package com.kwery.tests.services.job.launch.dependets;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobWithDependentsSqlQueryKilledTest extends JobServiceLaunchWithDependentsKilledJobTest {
    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() ->
                !getJobExecutionModels(JobExecutionModel.Status.ONGOING).isEmpty() && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() == 2));

        jobService.stopExecution(getSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.ONGOING).get(0).getExecutionId());
        waitAtMost(1, MINUTES).until(() -> getSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.ONGOING).isEmpty());

        SECONDS.sleep(30);

        jobService.stopExecution(getSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.ONGOING).get(0).getExecutionId());
        waitAtMost(1, MINUTES).until(() ->
                getSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.ONGOING).isEmpty() && getJobExecutionModels(JobExecutionModel.Status.FAILURE).size() == 1
        );

        assertThat(getJobExecutionModels(dependentJobModel.getId()), hasSize(0));

        assertEmailDoesNotExists();
    }
}
