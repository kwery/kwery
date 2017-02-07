package com.kwery.tests.services.job.launch;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobSqlQueryKilledJobCompletesTest extends JobServiceLaunchJobKilledTest {
    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() ->
                !getJobExecutionModels(jobModel.getId(), JobExecutionModel.Status.ONGOING).isEmpty() && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() == 2));

        jobService.stopExecution(getSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.ONGOING).get(0).getExecutionId());
        waitAtMost(1, MINUTES).until(() -> getSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.ONGOING).isEmpty());

        SECONDS.sleep(30);

        assertThat("Other SQL query is still executing", getSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.ONGOING).size(), is(1));
        assertThat("Job is still executing", getJobExecutionModels(JobExecutionModel.Status.ONGOING), hasSize(1));

        jobService.stopExecution(getSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.ONGOING).get(0).getExecutionId());
        waitAtMost(1, MINUTES).until(() ->
                getSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.ONGOING).isEmpty() && getJobExecutionModels(JobExecutionModel.Status.FAILURE).size() == 1
        );


        assertJobExecutionModel(JobExecutionModel.Status.FAILURE);
        assertSqlQueryExecutionModel(sqlQueryId0, SqlQueryExecutionModel.Status.KILLED);
        assertSqlQueryExecutionModel(sqlQueryId1, SqlQueryExecutionModel.Status.KILLED);

        assertEmailDoesNotExists();
    }
}
