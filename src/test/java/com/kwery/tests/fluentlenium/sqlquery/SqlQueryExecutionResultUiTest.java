package com.kwery.tests.fluentlenium.sqlquery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
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

import java.util.List;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.models.SqlQueryModel.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SqlQueryExecutionResultUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected SqlQueryExecutionResultPage page;
    protected String jsonResult;

    @Before
    public void setUpSqlQueryExecutionResultTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonResult = objectMapper.writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("username", "password"),
                        ImmutableList.of("raju", "cool"),
                        ImmutableList.of("kaju", "dude")
                )
        );

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "thik-3456-lkdsjkfkl-lskjdfkl", 1475158740747l, jsonResult, SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        page = createPage(SqlQueryExecutionResultPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void test() {
        page.waitForResultHeader(2);
        assertThat(page.resultHeader(), hasSize(2));

        List<String> resultHeaders = page.resultHeader();

        assertThat(resultHeaders.get(0), is("username"));
        assertThat(resultHeaders.get(1), is("password"));

        List<String> firstRow = page.resultContent().get(0);

        assertThat(firstRow.get(0), is("raju"));
        assertThat(firstRow.get(1), is("cool"));

        List<String> secondRow = page.resultContent().get(1);

        assertThat(secondRow.get(0), is("kaju"));
        assertThat(secondRow.get(1), is("dude"));
    }
}
