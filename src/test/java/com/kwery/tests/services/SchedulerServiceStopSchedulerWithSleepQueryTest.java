package com.kwery.tests.services;

import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.services.scheduler.SqlQueryTaskSchedulerHolder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryModel.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SchedulerServiceStopSchedulerWithSleepQueryTest extends RepoDashTestBase {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SchedulerService schedulerService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder;

    @Before
    public void setUpSchedulerServiceStopSchedulerWithRegularQueryTest() {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "testQuery0", "select sleep(84000)", 1)
                                .build()
                )
        ).launch();

        schedulerService = getInstance(SchedulerService.class);
        schedulerService.schedule(getInstance(SqlQueryDao.class).getById(1));

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
        sqlQueryTaskSchedulerHolder = getInstance(SqlQueryTaskSchedulerHolder.class);
    }

    @Test
    public void test() throws InterruptedException {
        Awaitility.waitAtMost(70, SECONDS).until(() -> !sqlQueryExecutions().isEmpty());

        schedulerService.stopScheduler(1);

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(1);

        long countBeforeSleep = sqlQueryExecutionDao.count(filter);
        SECONDS.sleep(70);
        long countAfterSleep = sqlQueryExecutionDao.count(filter);

        assertThat(sqlQueryTaskSchedulerHolder.get(1), empty());
        assertThat("No new query was executed after stopping the scheduler", countBeforeSleep, is(countAfterSleep));
    }

    private List<SqlQueryExecutionModel> sqlQueryExecutions() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(1);
        return sqlQueryExecutionDao.filter(filter);
    }
}
