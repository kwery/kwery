package com.kwery.tests.services.job.schedule;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobServiceScheduleJobSuccessTest extends JobServiceJobSetUpAbstractTest {
    @Test
    public void test() throws Exception {
        jobService.schedule(jobModel.getId());

        waitAtMost(130, SECONDS).until(() -> getJobExecutionModels(JobExecutionModel.Status.SUCCESS).size() >= 2);

        assertJobExecutionModels(JobExecutionModel.Status.SUCCESS, 2);

        assertSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.SUCCESS, 2);
        assertSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.SUCCESS, 2);

        assertReportEmailExists();
    }

    @Override
    protected String getQuery() {
        return "select User from mysql.user where User = 'root'";
    }
}
