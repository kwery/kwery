package com.kwery.tests.services.job.launch.dependets;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpWithDependentsAbstractTest;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;

public class JobServiceLaunchJobWithDependentsSuccessTest extends JobServiceJobSetUpWithDependentsAbstractTest {
    @Test
    public void test() {
        jobService.launch(jobModel.getId());

        waitAtMost(2, MINUTES).until(() -> getJobExecutionModels(JobExecutionModel.Status.SUCCESS).size() == 2);

        assertJobExecutionModel(JobExecutionModel.Status.SUCCESS, dependentJobModel.getId());

        assertSqlQueryExecutionModel(sqlQueryId2, SqlQueryExecutionModel.Status.SUCCESS);
        assertSqlQueryExecutionModel(sqlQueryId3, SqlQueryExecutionModel.Status.SUCCESS);
    }

    @Override
    protected String getQuery() {
        return "select User from mysql.user where User = 'root'";
    }
}
