package com.kwery.tests.services.job.launch;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobKilledTest extends JobServiceJobSetUpAbstractTest {
    protected boolean mailTest = true;

    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() ->
                !getJobExecutionModels(JobExecutionModel.Status.ONGOING).isEmpty() && (getSqlQueryExecutionModels(SqlQueryExecutionModel.Status.ONGOING).size() == 2));

        JobExecutionModel jobExecutionModel = getJobExecutionModels(JobExecutionModel.Status.ONGOING).get(0);

        jobService.stopExecution(jobExecutionModel.getExecutionId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(JobExecutionModel.Status.KILLED).isEmpty());

        assertJobExecutionModel(JobExecutionModel.Status.KILLED);

        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            assertSqlQueryExecutionModel(sqlQueryModel.getId(), SqlQueryExecutionModel.Status.KILLED);
        }

        if (isMailTest()) {
            assertEmailDoesNotExists();
        }
    }

    @Override
    protected String getQuery() {
        return "select sleep(100000)";
    }

    protected void disableMailTest() {
        this.mailTest = false;
    }

    public boolean isMailTest() {
        return mailTest;
    }
}
