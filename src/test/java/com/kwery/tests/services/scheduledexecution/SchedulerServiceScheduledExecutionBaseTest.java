package com.kwery.tests.services.scheduledexecution;

import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryTaskSchedulerHolder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryModel.CRON_EXPRESSION_COLUMN;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public abstract class SchedulerServiceScheduledExecutionBaseTest extends RepoDashTestBase {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SchedulerService schedulerService;
    protected SqlQueryDao sqlQueryDao;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder;
    protected int successQueryId = 1;
    protected int sleepQueryId = 2;
    protected int failQueryId = 3;

    protected String recipientEmail = "foo@getkwery.com";

    @Before
    public void setUpSchedulerServiceScheduledExecutionBaseTest () {
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
                                .values(successQueryId, "* * * * *", "selectQuery", "select User from mysql.user where User = 'root'", 1)
                                .values(sleepQueryId, "* * * * *", "sleepQuery", "select sleep(100000)", 1)
                                .values(failQueryId, "* * * * *", "failQuery", "select * from foo", 1)
                                .build(),
                        insertInto(SqlQueryModel.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                                .columns(SqlQueryModel.COLUMN_QUERY_RUN_ID_FK, SqlQueryModel.COLUMN_EMAIL)
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
    }
}
