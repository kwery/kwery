package com.kwery.tests.services.job.launch;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobFailureTest extends JobServiceJobSetUpAbstractTest {
    protected boolean mailTest = true;

    @Test
    public void test() throws Exception {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(jobModel.getId(), JobExecutionModel.Status.FAILURE).isEmpty());

        assertJobExecutionModel(JobExecutionModel.Status.FAILURE, jobModel.getId());

        assertSqlQueryExecutionModel(sqlQueryId0, SqlQueryExecutionModel.Status.FAILURE);
        assertSqlQueryExecutionModel(sqlQueryId1, SqlQueryExecutionModel.Status.FAILURE);

        if (isMailTest()) {
            assertEmailDoesNotExists();
        }
    }

    @Override
    protected String getQuery() {
        return "select * from foo";
    }

    protected void disableMailTest() {
        this.mailTest = false;
    }

    public boolean isMailTest() {
        return mailTest;
    }
}
