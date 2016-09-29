package services;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import services.scheduler.OngoingQueryTask;
import services.scheduler.SchedulerService;
import util.RepoDashTestBase;
import util.TestUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static models.QueryRunExecution.Status.KILLED;
import static models.QueryRunExecution.Status.ONGOING;
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
    protected QueryRun queryRun;
    protected SchedulerService schedulerService;
    protected QueryRunExecutionDao queryRunExecutionDao;

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

        queryRun = new QueryRun();
        queryRun.setDatasource(datasource);
        queryRun.setCronExpression("* * * * *");
        queryRun.setLabel("test");
        queryRun.setQuery("select sleep(86440)");

        getInstance(QueryRunDao.class).save(queryRun);

        schedulerService = getInstance(SchedulerService.class);

        queryRunExecutionDao = getInstance(QueryRunExecutionDao.class);
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        long now = System.currentTimeMillis();

        schedulerService.schedule(queryRun);
        TimeUnit.MINUTES.sleep(3);

        List<OngoingQueryTask> ongoing = schedulerService.ongoingQueryTasks(queryRun.getId());
        int ongoingTasksSize = ongoing.size();
        assertThat(ongoingTasksSize, greaterThanOrEqualTo(2));

        List<String> ongoingExecutionIds = new LinkedList<>();

        for (OngoingQueryTask ongoingQueryTask : ongoing) {
            QueryRunExecution execution = queryRunExecutionDao.getByExecutionId(ongoingQueryTask.getExecutionId());
            assertThat(execution.getId(), greaterThan(0));
            assertThat(execution.getQueryRun().getId(), is(queryRun.getId()));
            assertThat(execution.getStatus(), is(ONGOING));
            assertThat(execution.getExecutionStart(), greaterThan(now));
            assertThat(execution.getExecutionEnd(), nullValue());
            assertThat(execution.getResult(), nullValue());
            ongoingExecutionIds.add(ongoingQueryTask.getExecutionId());
        }

        for (OngoingQueryTask ongoingQueryTask : ongoing) {
            schedulerService.stopExecution(queryRun.getId(), ongoingQueryTask.getExecutionId());
        }

        TimeUnit.MINUTES.sleep(2);

        ongoing = schedulerService.ongoingQueryTasks(queryRun.getId());
        for (OngoingQueryTask ongoingQueryTask : ongoing) {
            assertThat(ongoingExecutionIds, not(hasItem(ongoingQueryTask.getExecutionId())));
        }

        for (String ongoingExecutionId : ongoingExecutionIds) {
            QueryRunExecution execution = queryRunExecutionDao.getByExecutionId(ongoingExecutionId);
            assertThat(execution.getStatus(), is(KILLED));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getResult(), nullValue());
        }

        schedulerService.shutdownSchedulers();

        assertThat(schedulerService.getQueryRunSchedulerMap().get(queryRun.getId()).hasSchedulerStopped(), is(true));
    }

    @After
    public void tearDownSchedulerServiceTestOngoing() {
        cloudHost.teardown();
    }
}
