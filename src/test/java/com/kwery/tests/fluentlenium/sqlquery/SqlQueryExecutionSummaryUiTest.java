package com.kwery.tests.fluentlenium.sqlquery;

import com.google.common.collect.ImmutableList;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.IOException;
import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.sqlquery.SqlQueryExecutionSummaryPage.COLUMN_COUNT;
import static com.kwery.tests.util.Messages.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionSummaryUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryExecutionSummaryPage page;
    private SqlQueryModel sqlQueryModel;

    @Before
    public void setUpSqlQueryExecutionResultTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = objectMapper.writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("username", "password"),
                        ImmutableList.of("raju", "cool"),
                        ImmutableList.of("kaju", "dude")
                )
        );

        Datasource datasource = TestUtil.datasource();
        DbUtil.datasourceDbSetup(datasource);

        sqlQueryModel = TestUtil.sqlQueryModel(datasource);
        DbUtil.sqlQueryDbSetUp(sqlQueryModel);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "thik-3456-lkdsjkfkl-lskjdfkl", 1475158740747l, jsonResult, SUCCESS, sqlQueryModel.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        page = newInstance(SqlQueryExecutionSummaryPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render SQL query execution summary page");
        }
    }

    @Test
    public void test() {
        assertThat(page.getExecutionSummaryHeaders(), hasSize(COLUMN_COUNT));

        List<String> headers = page.getExecutionSummaryHeaders();

        assertThat(headers.get(0), is(LABEL_M));
        assertThat(headers.get(1), is(DATE_M));

        List<String> summary = page.getExecutionSummary().get(0);

        assertThat(summary.get(0), is(sqlQueryModel.getLabel()));
        assertThat(summary.get(1), is("Thu Sep 29 2016 20:09"));
        assertThat(summary.get(2), is(REPORT_M));

        assertThat(page.getReportLinks().get(0), containsString( String.format("/#sql-query/%d/execution/thik-3456-lkdsjkfkl-lskjdfkl", sqlQueryModel.getId())));
    }
}

