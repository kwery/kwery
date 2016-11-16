package com.kwery.tests.services;

import com.google.common.base.Optional;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import ninja.Bootstrap;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.scheduler.OngoingSqlQueryTask;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static com.kwery.models.SqlQueryExecution.Status.KILLED;
import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class SchedulerInteractionOnApplicationStartupAndStopTest extends RepoDashDaoTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected SqlQuery sqlQuery;
    protected SchedulerService schedulerService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
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

        sqlQuery = new SqlQuery();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setCronExpression("* * * * *");
        sqlQuery.setLabel("test");
        sqlQuery.setQuery("select sleep(86440)");

        getInstance(SqlQueryDao.class).save(sqlQuery);

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
        sqlQueryExecutionDao = bootstrap.getInjector().getInstance(SqlQueryExecutionDao.class);

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
            assertThat(execution.getExecutionStart(), greaterThan(start));
            assertThat(execution.getExecutionEnd(), nullValue());
            assertThat(execution.getResult(), nullValue());
            ongoingExecutionIds.add(ongoingSqlQueryTask.getExecutionId());
        }

        bootstrap.shutdown();

        for (String ongoingExecutionId : ongoingExecutionIds) {
            SqlQueryExecution execution = getInstance(SqlQueryExecutionDao.class).getByExecutionId(ongoingExecutionId);
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
