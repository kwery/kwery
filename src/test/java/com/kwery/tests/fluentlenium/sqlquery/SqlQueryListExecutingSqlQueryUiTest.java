package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import javax.sql.DataSource;
import java.util.List;

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
import static com.kwery.models.SqlQueryExecution.COLUMN_EXECUTION_END;
import static com.kwery.models.SqlQueryExecution.COLUMN_EXECUTION_ID;
import static com.kwery.models.SqlQueryExecution.COLUMN_EXECUTION_START;
import static com.kwery.models.SqlQueryExecution.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecution.COLUMN_RESULT;
import static com.kwery.models.SqlQueryExecution.COLUMN_STATUS;
import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static com.kwery.tests.util.Messages.DATASOURCE_M;
import static com.kwery.tests.util.Messages.KILL_QUERY_M;
import static com.kwery.tests.util.Messages.QUERY_M;
import static com.kwery.tests.util.Messages.START_M;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryListExecutingSqlQueryUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryExecutingListPage page;

    @Before
    public void setUpListOngoingQueriesTest() {
        DataSource datasource = DbUtil.getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecution.TABLE)
                                .columns(SqlQueryExecution.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, null, "sjfljkl", 1475215495171l, "status", SUCCESS, 1)
                                .values(2, null, "sjfljkl", 1475215495171l, null, ONGOING, 1)
                                .values(3, null, "sdjfklj", 1475215333445l, null, ONGOING, 1).build()
                )
        );

        dbSetup.launch();

        page = createPage(SqlQueryExecutingListPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        if (!page.isRendered()) {
            fail("Could not render list ongoing SQL queries page");
        }
    }

    @Test
    public void test() {
        List<FluentWebElement> headerColumns = $("#executingSqlQueriesTable thead th");
        assertThat(headerColumns, hasSize(4));

        assertThat(headerColumns.get(0).getText(), is(QUERY_M));
        assertThat(headerColumns.get(1).getText(), is(START_M));
        assertThat(headerColumns.get(2).getText(), is(DATASOURCE_M));
        assertThat(headerColumns.get(3).getText(), is(KILL_QUERY_M));

        List<FluentWebElement> columns = $("#executingSqlQueriesTable tr td");
        assertThat(columns, hasSize(8));

        assertThat(columns.get(0).getText(), is("testQuery"));
        assertThat(columns.get(1).getText(), is("Fri Sep 30 2016 11:32"));
        assertThat(columns.get(2).getText(), is("testDatasource"));
        assertThat(columns.get(3).getText().toLowerCase(), is(Messages.KILL_M.toLowerCase()));

        assertThat(columns.get(4).getText(), is("testQuery"));
        assertThat(columns.get(5).getText(), is("Fri Sep 30 2016 11:34"));
        assertThat(columns.get(6).getText(), is("testDatasource"));
        assertThat(columns.get(7).getText().toLowerCase(), is(Messages.KILL_M.toLowerCase()));
    }
}
