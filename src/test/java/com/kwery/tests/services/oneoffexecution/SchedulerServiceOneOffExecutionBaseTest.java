package com.kwery.tests.services.oneoffexecution;

import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.services.scheduler.OneOffSqlQueryTaskSchedulerReaper;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryTaskSchedulerHolder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.MySqlDocker;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.After;
import org.junit.Before;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class SchedulerServiceOneOffExecutionBaseTest extends RepoDashTestBase {
    protected MySqlDocker mySqlDocker;
    protected SchedulerService schedulerService;
    protected SqlQueryDao sqlQueryDao;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder;
    protected OneOffSqlQueryTaskSchedulerReaper oneOffSqlQueryTaskSchedulerReaper;

    protected int successQueryId = 1;
    protected int sleepQueryId = 2;
    protected int failQueryId = 3;

    protected String recipientEmail = "foo@getkwery.com";

    @Before
    public void setUpSchedulerServiceOneOffExecutionBaseTest() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();

        Datasource datasource = mySqlDocker.datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(successQueryId, "", "selectQuery", "select User from mysql.user where User = 'root'", 1)
                                .values(sleepQueryId, "", "sleepQuery", "select sleep(100000)", 1)
                                .values(failQueryId, "", "failQuery", "select * from foo", 1)
                                .build(),
                        insertInto(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                                .columns(SqlQuery.COLUMN_QUERY_RUN_ID_FK, SqlQuery.COLUMN_EMAIL)
                                .values(successQueryId, recipientEmail)
                                .values(sleepQueryId, recipientEmail)
                                .values(failQueryId, recipientEmail)
                                .build()
                )
        ).launch();

        schedulerService = getInstance(SchedulerService.class);
        sqlQueryDao = getInstance(SqlQueryDao.class);
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);

        sqlQueryTaskSchedulerHolder = getInstance(SqlQueryTaskSchedulerHolder.class);
        oneOffSqlQueryTaskSchedulerReaper = getInstance(OneOffSqlQueryTaskSchedulerReaper.class);
    }

    @After
    public void tearDownSchedulerServiceTestDirectExecution() {
        mySqlDocker.tearDown();
    }
}
