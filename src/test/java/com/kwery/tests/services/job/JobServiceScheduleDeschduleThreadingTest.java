package com.kwery.tests.services.job;

import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobServiceScheduleDeschduleThreadingTest extends RepoDashTestBase {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;

    protected JobService jobService;

    protected static int jobId = 0;

    @Before
    public void setUpJobApiControllerDeleteJobTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(1);
        datasourceDbSetup(datasource);
        jobService = getInstance(JobService.class);
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 50; ++i) {
            executorService.submit(() -> {
                try {
                    latch.await();
                    scheduleDeschedule();
                } catch (InterruptedException ie) { }
            });
        }
        latch.countDown();

        executorService.shutdown();

        if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
            logger.info("Executor service did not shutdown normally, forcing shutdown");
            executorService.shutdownNow();
        }

        assertThat(jobService.getJobIdSchedulerIdMap().isEmpty(), is(true));
    }

    protected void scheduleDeschedule() {
        JobModel jobModel = jobModelWithoutDependents();
        jobModel.setParameterCsv("");

        jobModel.setCronExpression("* * * * *");

        int id = jobId++;

        jobModel.setId(id);
        jobDbSetUp(jobModel);

        SqlQueryModel sqlQueryModel = sqlQueryModel();
        sqlQueryModel.setDatasource(datasource);
        sqlQueryModel.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryModel.setId(id);

        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);
        jobService.schedule(id);
        jobService.deschedule(id);
    }
}
