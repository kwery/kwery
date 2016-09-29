package services;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import services.scheduler.OngoingSqlQueryTask;
import services.scheduler.SchedulerService;
import util.RepoDashTestBase;
import util.TestUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static models.SqlQueryExecution.Status.KILLED;
import static models.SqlQueryExecution.Status.ONGOING;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SchedulerServiceTestOngoingAndCancel extends RepoDashTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected SqlQuery sqlQuery;
    protected SchedulerService schedulerService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    @Before
    public void setUpSchedulerServiceTestOngoing() {
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
        sqlQuery.setQuery("select sleep(86440)");

        getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = getInstance(SchedulerService.class);

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        schedulerService.schedule(sqlQuery);
        TimeUnit.MINUTES.sleep(3);

        List<OngoingSqlQueryTask> ongoing = schedulerService.ongoingQueryTasks(sqlQuery.getId());
        int ongoingTasksSize = ongoing.size();
        assertThat(ongoingTasksSize, greaterThanOrEqualTo(2));

        List<String> ongoingExecutionIds = new LinkedList<>();

        for (OngoingSqlQueryTask ongoingSqlQueryTask : ongoing) {
            SqlQueryExecution execution = sqlQueryExecutionDao.getByExecutionId(ongoingSqlQueryTask.getExecutionId());
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getSqlQuery().getId(), is(sqlQuery.getId()));
            assertThat(execution.getStatus(), is(ONGOING));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), nullValue());
            assertThat(execution.getResult(), nullValue());
            ongoingExecutionIds.add(ongoingSqlQueryTask.getExecutionId());
        }

        for (OngoingSqlQueryTask ongoingSqlQueryTask : ongoing) {
            schedulerService.stopExecution(sqlQuery.getId(), ongoingSqlQueryTask.getExecutionId());
        }

        TimeUnit.MINUTES.sleep(2);

        ongoing = schedulerService.ongoingQueryTasks(sqlQuery.getId());
        for (OngoingSqlQueryTask ongoingSqlQueryTask : ongoing) {
            assertThat(ongoingExecutionIds, not(hasItem(ongoingSqlQueryTask.getExecutionId())));
        }

        for (String ongoingExecutionId : ongoingExecutionIds) {
            SqlQueryExecution execution = sqlQueryExecutionDao.getByExecutionId(ongoingExecutionId);
            assertThat(execution.getStatus(), is(KILLED));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getResult(), nullValue());
        }

        schedulerService.shutdownSchedulers();

        assertThat(schedulerService.getQueryRunSchedulerMap().get(sqlQuery.getId()).hasSchedulerStopped(), is(true));
    }

    @After
    public void tearDownSchedulerServiceTestOngoing() {
        cloudHost.teardown();
    }
}
