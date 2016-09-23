package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import dao.DatasourceDao;
import dao.QueryRunDao;
import dao.QueryRunExecutionDao;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import util.RepoDashTestBase;
import util.TestUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static models.QueryRunExecution.Status.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SchedulerServiceTestScheduleDao extends RepoDashTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected QueryRun queryRun;
    protected SchedulerService schedulerService;
    protected QueryRunExecutionDao queryRunExecutionDao;

    @Before
    public void setUpSchedulerServiceTestScheduler() {
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();
        String mysqlHost = cloudHost.getHostName();
        int port = cloudHost.getPort(3306);

        if (!TestUtil.waitForMysql(mysqlHost, port)) {
            fail("MySQL docker service is not up");
        }

        datasource = new Datasource();
        datasource.setUrl(mysqlHost);
        datasource.setPort(port);
        datasource.setLabel("test");
        datasource.setUsername("root");
        datasource.setPassword("root");

        getInstance(DatasourceDao.class).save(datasource);

        queryRun = new QueryRun();
        queryRun.setDatasource(datasource);
        queryRun.setCronExpression("* * * * *");
        queryRun.setLabel("test");
        queryRun.setQuery("select user from mysql.user where user in ('root')");

        getInstance(QueryRunDao.class).save(queryRun);

        schedulerService = getInstance(SchedulerService.class);

        queryRunExecutionDao = getInstance(QueryRunExecutionDao.class);
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        schedulerService.schedule(queryRun);
        TimeUnit.MINUTES.sleep(3);
        List<QueryRunExecution> executions = queryRunExecutionDao.getByQueryRunId(queryRun.getId());
        assertThat(executions.size(), greaterThanOrEqualTo(2));

        String expectedResult = new ObjectMapper().writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("user"),
                        ImmutableList.of("root")
                )
        );

        for (QueryRunExecution execution : executions) {
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getQueryRun().getId(), is(queryRun.getId()));
            assertThat(execution.getStatus(), is(SUCCESS));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getExecutionId().length(), greaterThan(0));
            assertThat(execution.getResult(), is(expectedResult));
        }
    }

    @After
    public void tearDownSchedulerServiceTestScheduler() {
        cloudHost.teardown();
    }
}
