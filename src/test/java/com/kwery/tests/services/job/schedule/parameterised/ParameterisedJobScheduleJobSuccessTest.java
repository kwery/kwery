package com.kwery.tests.services.job.schedule.parameterised;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.services.job.JobServiceJobSetUpAbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

public class ParameterisedJobScheduleJobSuccessTest extends JobServiceJobSetUpAbstractTest {
    @Before
    public void setUp() {
        JobModel jobModel = jobDao.getJobById(this.jobModel.getId());
        jobModel.setParameterCsv("user\nroot\nroot");
        jobDao.save(jobModel);
    }

    @Test
    public void test() throws Exception {
        jobService.schedule(jobModel.getId());
        waitAtMost(130, SECONDS).until(() -> getJobExecutionModels(JobExecutionModel.Status.SUCCESS).size() >= 4);
        assertJobExecutionModels(JobExecutionModel.Status.SUCCESS, 4);
        TimeUnit.SECONDS.sleep(130);
        assertSqlQueryExecutionModels(sqlQueryId0, SqlQueryExecutionModel.Status.SUCCESS, 4);
        assertSqlQueryExecutionModels(sqlQueryId1, SqlQueryExecutionModel.Status.SUCCESS, 4);
        assertReportEmailExists();
    }

    @Override
    protected String getQuery() {
        return "select User from mysql.user where User = :user";
    }
}
