package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dao.SqlQueryExecutionDao;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import services.scheduler.SqlQueryExecutionSearchFilter;
import util.RepoDashTestBase;
import util.TestUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SchedulerServiceTestScheduleDao extends RepoDashTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected SqlQuery sqlQuery;
    protected SchedulerService schedulerService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

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

        sqlQuery = new SqlQuery();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setCronExpression("* * * * *");
        sqlQuery.setLabel("test");
        sqlQuery.setQuery("select user from mysql.user where user in ('root')");

        getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = getInstance(SchedulerService.class);

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        schedulerService.schedule(sqlQuery);
        TimeUnit.MINUTES.sleep(3);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQuery.getId());

        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);
        assertThat(executions.size(), greaterThanOrEqualTo(2));

        String expectedResult = new ObjectMapper().writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("user"),
                        ImmutableList.of("root")
                )
        );

        for (SqlQueryExecution execution : executions) {
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getSqlQuery().getId(), is(sqlQuery.getId()));
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
