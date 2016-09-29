package services;

import com.google.common.base.Optional;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import dao.DatasourceDao;
import dao.QueryRunDao;
import dao.QueryRunExecutionDao;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import ninja.Bootstrap;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.OngoingQueryTask;
import services.scheduler.SchedulerService;
import util.RepoDashDaoTestBase;
import util.TestUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static models.QueryRunExecution.Status.KILLED;
import static models.QueryRunExecution.Status.ONGOING;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class SchedulerInteractionOnApplicationStartupAndStopTest extends RepoDashDaoTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected QueryRun queryRun;
    protected SchedulerService schedulerService;
    protected QueryRunExecutionDao queryRunExecutionDao;
    protected Bootstrap bootstrap;

    protected long start = System.currentTimeMillis();

    @Before
    public void setUpSchedulerInteractionOnApplicationStartupAndStopTest() {
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

        Optional<NinjaMode> mode = NinjaModeHelper.determineModeFromSystemProperties();
        NinjaMode ninjaMode = mode.isPresent() ? mode.get() : NinjaMode.test;
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);

        //We need to keep the already created tables and data intact
        ninjaProperties.setProperty("ninja.migration.run", String.valueOf(false));

        bootstrap = new Bootstrap(ninjaProperties);
        bootstrap.boot();
    }

    @Test
    public void test() throws InterruptedException {
        schedulerService = bootstrap.getInjector().getInstance(SchedulerService.class);
        queryRunExecutionDao = bootstrap.getInjector().getInstance(QueryRunExecutionDao.class);

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
            assertThat(execution.getExecutionStart(), greaterThan(start));
            assertThat(execution.getExecutionEnd(), nullValue());
            assertThat(execution.getResult(), nullValue());
            ongoingExecutionIds.add(ongoingQueryTask.getExecutionId());
        }

        bootstrap.shutdown();

        for (String ongoingExecutionId : ongoingExecutionIds) {
            QueryRunExecution execution = getInstance(QueryRunExecutionDao.class).getByExecutionId(ongoingExecutionId);
            assertThat(execution.getStatus(), is(KILLED));
            assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
            assertThat(execution.getResult(), nullValue());
        }
    }

    @After
    public void tearDownSchedulerInteractionOnApplicationStartupAndStopTest() {
        cloudHost.teardown();
    }
}
