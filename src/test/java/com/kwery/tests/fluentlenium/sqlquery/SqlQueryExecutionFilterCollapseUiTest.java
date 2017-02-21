package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
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

import java.util.UUID;

import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.FAILURE;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionFilterCollapseUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryExecutionListPage page;

    @Before
    public void setUpListSqlQueryExecutionTest() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_ERROR, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(3, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(4, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(5, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        page = newInstance(SqlQueryExecutionListPage.class);
        page.setSqlQueryId(sqlQueryModel0.getId());

        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void testDefaultFilterState() {
        assertThat(page.isFilterCollapsed(), is(true));
    }

    @Test
    public void testUseFilterAndPaginateFilterOpen() {
        page.clickFilter();
        page.fillResultCount(1);
        page.clickNext();
        assertThat(page.isFilterCollapsed(), is(false));
    }

    @Test
    public void testUseFilterAndCloseAndPaginate() {
        page.clickFilter();
        page.fillResultCount(1);
        page.clickFilter();
        page.clickNext();
        assertThat(page.isFilterCollapsed(), is(false));
    }

    @Test
    public void testPaginateWithoutUsingFilter() {
        page.clickNext();
        assertThat(page.isFilterCollapsed(), is(true));
        page.clickPrevious();
        assertThat(page.isFilterCollapsed(), is(true));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
