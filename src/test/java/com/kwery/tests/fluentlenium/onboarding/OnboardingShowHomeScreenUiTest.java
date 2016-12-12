package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import javax.sql.DataSource;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class OnboardingShowHomeScreenUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Before
    public void setUpOnboardingShowExecutingSqlQueriesPageTest() throws InterruptedException {
        DataSource datasource = getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "testQuery", "select * from foo", 1).build()
                )
        );

        dbSetup.launch();

        goTo(ninjaServerRule.getServerUrl() + "/");
    }

    @Test
    public void test() {
        await().atMost(TIMEOUT_SECONDS).until(".f-execution-summary-table").isDisplayed();
    }
}
