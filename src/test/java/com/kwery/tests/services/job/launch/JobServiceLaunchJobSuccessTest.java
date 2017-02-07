package com.kwery.tests.services.job.launch;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobSuccessTest extends JobServiceJobSetUpAbstractTest {
    @Test
    public void test() throws Exception {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(JobExecutionModel.Status.SUCCESS).isEmpty());

        assertJobExecutionModel(JobExecutionModel.Status.SUCCESS);

        assertSqlQueryExecutionModel(sqlQueryId0, SqlQueryExecutionModel.Status.SUCCESS);
        assertSqlQueryExecutionModel(sqlQueryId1, SqlQueryExecutionModel.Status.SUCCESS);

        assertReportEmailExists();
    }

    @Override
    protected String getQuery() {
        return "select User from mysql.user where User = 'root'";
    }
}
