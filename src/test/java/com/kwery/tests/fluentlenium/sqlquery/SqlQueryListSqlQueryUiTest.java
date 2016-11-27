package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

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
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryListSqlQueryUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryListPage page;

    @Before
    public void setUpListSqlQueriesPageTest() {
        UserTableUtil userTableUtil = new UserTableUtil();
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "*", "testQuery0", "select * from foo", 1)
                                .values(2, "* *", "testQuery1", "select * from bar", 1)
                                .build()
                )
        );
        dbSetup.launch();

        page = createPage(SqlQueryListPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void test() {
        page.waitForRows(2);

        List<String> headers = page.headers();

        assertThat(headers, hasSize(5));

        assertThat(headers.get(0), is(Messages.LABEL_M));
        assertThat(headers.get(1), is(Messages.CRON_EXPRESSION_M));
        assertThat(headers.get(2), is(Messages.QUERY_M));
        assertThat(headers.get(3), is(Messages.DATASOURCE_M));

        List<List<String>> rows = page.rows();

        assertThat(rows, hasSize(2));

        List<String> firstRow = rows.get(0);

        assertThat(firstRow.get(0), is("testQuery0"));
        assertThat(firstRow.get(1), is("*"));
        assertThat(firstRow.get(2), is("select * from foo"));
        assertThat(firstRow.get(3), is("testDatasource"));

        List<String> secondRow = rows.get(1);

        assertThat(secondRow.get(0), is("testQuery1"));
        assertThat(secondRow.get(1), is("* *"));
        assertThat(secondRow.get(2), is("select * from bar"));
        assertThat(secondRow.get(3), is("testDatasource"));
    }
}
