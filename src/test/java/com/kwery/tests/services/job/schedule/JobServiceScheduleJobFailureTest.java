package com.kwery.tests.services.job.schedule;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

public class JobServiceScheduleJobFailureTest extends JobServiceJobSetUpAbstractTest {
    protected boolean mailTest = true;

    @Test
    public void test() throws Exception {
        jobService.schedule(jobModel.getId());
        waitAtMost(130, SECONDS).until(() -> getJobExecutionModels(JobExecutionModel.Status.FAILURE).size() >= 2);

        assertJobExecutionModels(JobExecutionModel.Status.FAILURE, 2);

        assertSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.FAILURE, 2);
        assertSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.FAILURE, 2);

        if (this.isMailTest()) {
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
