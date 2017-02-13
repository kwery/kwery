package com.kwery.tests.services.job.launch.jobrulemodel;

import com.kwery.models.JobRuleModel;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.jobRuleModel;
import static java.util.concurrent.TimeUnit.MINUTES;

public class JobServiceLaunchJobRuleSequentialExecutionStopOnExecutionFailureTest extends JobServiceLaunchJobJobRuleModelAbstractTest {
    @Override
    public String getQuery0() {
        return "select User from mysql.user where User = 'root'";
    }

    @Override
    public String getQuery1() {
        return "select * from foo";
    }

    @Override
    public String getQuery2() {
        return "select User from mysql.user where User = 'root'";
    }

    @Override
    public JobRuleModel getJobRuleMode() {
        JobRuleModel jobRuleModel = jobRuleModel();
        jobRuleModel.setSequentialSqlQueryExecution(true);
        jobRuleModel.setStopExecutionOnSqlQueryFailure(true);
        return jobRuleModel;
    }

    @Test
    public void test() {
        jobService.launch(jobModel.getId());
        waitUntilExecutions(1, MINUTES, 1);
        assertQueriesWereExecutedSequentially(2);
    }
}
