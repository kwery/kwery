package com.kwery.tests.services;

import com.google.common.base.Optional;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.OngoingSqlQueryTask;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.RepoDashDaoTestBase;
import ninja.Bootstrap;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kwery.models.SqlQueryExecution.Status.KILLED;
import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

public class SchedulerInteractionOnApplicationStartupAndStopTest extends RepoDashDaoTestBase {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;
    protected SqlQueryModel sqlQuery;
    protected SchedulerService schedulerService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected Bootstrap bootstrap;

    protected long start = System.currentTimeMillis();

    @Before
    public void setUpSchedulerInteractionOnApplicationStartupAndStopTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        getInstance(DatasourceDao.class).save(datasource);

        sqlQuery = new SqlQueryModel();
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

        Awaitility.waitAtMost(3, TimeUnit.MINUTES).until(() -> schedulerService.ongoingQueryTasks(sqlQuery.getId()).size() >= 2);

        List<OngoingSqlQueryTask> ongoing = schedulerService.ongoingQueryTasks(sqlQuery.getId());
        int ongoingTasksSize = ongoing.size();

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
}
