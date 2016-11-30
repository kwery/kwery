package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
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
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryDependsOnSqlQueryCronExpressionToggleAddUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryAddPage addSqlQueryPage;

    @Before
    public void setUpAddSqlQueryDependsOnSqlQueryCronExpressionToggleTest() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1)
                                .build()
                )
        );
        dbSetup.launch();

        addSqlQueryPage = createPage(SqlQueryAddPage.class);
        addSqlQueryPage.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(addSqlQueryPage);
        if (!addSqlQueryPage.isRendered()) {
            fail("Add SQL query page not rendered");
        }
    }

    @Test
    public void test() {
        assertThat(addSqlQueryPage.isDependsOnSqlQueryEnabled(), is(false));
        assertThat(addSqlQueryPage.isCronExpressionEnabled(), is(true));
        assertThat(addSqlQueryPage.enableDependsOnSqlQueryLinkDisplayed(), is(true));
        assertThat(addSqlQueryPage.enableCronExpressionLinkDisplayed(), is(false));

        addSqlQueryPage.clickEnableDependsOnSqlQuery();
        addSqlQueryPage.waitForEnableDependsOnSqlQuery();
        assertThat(addSqlQueryPage.isCronExpressionEnabled(), is(false));

        addSqlQueryPage.clickEnableCronExpression();
        addSqlQueryPage.waitForEnableCronExpression();
        assertThat(addSqlQueryPage.isDependsOnSqlQueryEnabled(), is(false));
    }
}
