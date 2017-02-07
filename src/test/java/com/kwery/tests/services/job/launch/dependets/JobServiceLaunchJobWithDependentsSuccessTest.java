package com.kwery.tests.services.job.launch.dependets;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobWithDependentsSuccessTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() throws Exception {
        jobService.launch(jobModel.getId());

        waitAtMost(2, MINUTES).until(() -> getJobExecutionModels(JobExecutionModel.Status.SUCCESS).size() == 2);

        assertJobExecutionModel(JobExecutionModel.Status.SUCCESS, dependentJobModel.getId());

        assertSqlQueryExecutionModel(sqlQueryId2, SqlQueryExecutionModel.Status.SUCCESS);
        assertSqlQueryExecutionModel(sqlQueryId3, SqlQueryExecutionModel.Status.SUCCESS);

        assertReportEmailExists();
    }

    @Override
    protected String getQuery() {
        return "select User from mysql.user where User = 'root'";
    }
}
