package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

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
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SqlQueryUpdateUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SqlQueryUpdatePage page;

    protected Datasource datasource;

    @Before
    public void setUpUpdateSqlQueryPageTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .values(2, "testDatasource1", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "selectQuery", "select * from mysql.db", 1)
                                .values(2, "* * * * *", "sleepQuery", "select sleep(86400)", 1)
                                .build()
                )
        ).launch();

        ninjaServerRule.getInjector().getInstance(SchedulerService.class).schedule(ninjaServerRule.getInjector().getInstance(SqlQueryDao.class).getById(1));

        page = createPage(SqlQueryUpdatePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update user page");
        }
    }

    @Test
    public void testUpdateSuccess() {
        page.waitForForm("label", "selectQuery");

        assertThat(page.actionLabel().toLowerCase(), is(Messages.UPDATE_M.toLowerCase()));

        page.fillLabel("foo");
        page.fillQuery("select sleep(86400)");
        page.fillCronExpression("5 * * * *");
        page.selectDatasource(1);
        page.submit();

        page.waitForSuccessMessage();
    }

    @Test
    public void testDuplicateLabel() {
        page.waitForForm("label", "selectQuery");

        assertThat(page.actionLabel().toLowerCase(), is(Messages.UPDATE_M.toLowerCase()));

        page.fillLabel("sleepQuery");
        //TODO - For some reason, label is being sent to the server as null if the other fields are not filled in, debug this
        page.fillQuery("select sleep(86400)");
        page.fillCronExpression("5 * * * *");
        page.selectDatasource(1);
        page.submit();

        page.waitForDuplicateLabelMessage("sleepQuery");
    }
}
