package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.ONGOING;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
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
    private SqlQueryModel sqlQueryModel;
    private Datasource datasource;

    @Before
    public void setUpListOngoingQueriesTest() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, null, "sjfljkl", 1475215495171l, "status", SUCCESS, sqlQueryModel.getId())
                                .values(2, null, "sjfljkl", 1475215495171l, null, ONGOING, sqlQueryModel.getId())
                                .values(3, null, "sdjfklj", 1475215333445l, null, ONGOING, sqlQueryModel.getId()).build()
                )
        );

        dbSetup.launch();

        page = newInstance(SqlQueryExecutingListPage.class);
        goTo(page);
        if (!page.isRendered()) {
            fail("Could not render list ongoing SQL queries page");
        }
    }

    @Test
    public void test() {
        List<FluentWebElement> headerColumns = $("#executingSqlQueriesTable thead th");
        assertThat(headerColumns, hasSize(3));

        assertThat(headerColumns.get(0).text(), is(QUERY_M));
        assertThat(headerColumns.get(1).text(), is(START_M));
        assertThat(headerColumns.get(2).text(), is(DATASOURCE_M));

        List<FluentWebElement> columns = $("#executingSqlQueriesTable tr td");
        assertThat(columns, hasSize(6));

        assertThat(columns.get(0).text(), is(sqlQueryModel.getLabel()));
        assertThat(columns.get(1).text(), is("Fri Sep 30 2016 11:32"));
        assertThat(columns.get(2).text(), is(datasource.getLabel()));

        assertThat(columns.get(3).text(), is(sqlQueryModel.getLabel()));
        assertThat(columns.get(4).text(), is("Fri Sep 30 2016 11:34"));
        assertThat(columns.get(5).text(), is(datasource.getLabel()));
    }
}
