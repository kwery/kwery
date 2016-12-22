package com.kwery.tests.services.job.schedule.dependents;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobServiceScheduleJobWithDependentsSuccessTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() {
        jobService.schedule(jobModel.getId());

        waitAtMost(150, SECONDS).until(() -> getJobExecutionModels(dependentJobModel.getId(), JobExecutionModel.Status.SUCCESS).size() >= 2);

        assertJobExecutionModels(JobExecutionModel.Status.SUCCESS, 2, dependentJobModel.getId());

        assertSqlQueryExecutionModels(sqlQueryId2, SqlQueryExecutionModel.Status.SUCCESS, 2);
        assertSqlQueryExecutionModels(sqlQueryId3, SqlQueryExecutionModel.Status.SUCCESS, 2);

        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();
        assertThat(mail, notNullValue());
    }

    @Override
    protected String getQuery() {
        return "select User from mysql.user where User = 'root'";
    }
}
