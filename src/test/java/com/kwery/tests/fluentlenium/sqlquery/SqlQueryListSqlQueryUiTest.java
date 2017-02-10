package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryListSqlQueryUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryListPage page;
    private SqlQueryModel sqlQueryModel0;
    private SqlQueryModel sqlQueryModel1;
    private Datasource datasource;

    @Before
    public void setUpListSqlQueriesPageTest() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryModel0.setId(1);
        sqlQueryDbSetUp(sqlQueryModel0);

        sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryModel1.setId(2);
        sqlQueryDbSetUp(sqlQueryModel1);

        page = newInstance(SqlQueryListPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void test() {
        page.waitForRows(2);

        List<String> headers = page.headers();

        assertThat(headers, hasSize(3));

        assertThat(headers.get(0), is(Messages.LABEL_M));
        assertThat(headers.get(1), is(Messages.QUERY_M));
        assertThat(headers.get(2), is(Messages.DATASOURCE_M));

        List<List<String>> rows = page.rows();

        assertThat(rows, hasSize(2));

        List<String> firstRow = rows.get(0);

        assertThat(firstRow.get(0), is(sqlQueryModel0.getLabel()));
        assertThat(firstRow.get(1), is(sqlQueryModel0.getQuery()));
        assertThat(firstRow.get(2), is(datasource.getLabel()));

        List<String> secondRow = rows.get(1);

        assertThat(secondRow.get(0), is(sqlQueryModel1.getLabel()));
        assertThat(secondRow.get(1), is(sqlQueryModel1.getQuery()));
        assertThat(secondRow.get(2), is(datasource.getLabel()));
    }
}
