package com.kwery.tests.fluentlenium.sqlquery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
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
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SqlQueryExecutionResultUiTest extends RepoDashFluentLeniumTest {
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

        UserTableUtil userTableUtil = new UserTableUtil();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation(),
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecution.TABLE)
                                .columns(SqlQueryExecution.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "thik-3456-lkdsjkfkl-lskjdfkl", 1475158740747l, jsonResult, SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }
        loginPage.submitForm(userTableUtil.firstRow().getUsername(), userTableUtil.firstRow().getPassword());
        loginPage.waitForSuccessMessage(userTableUtil.firstRow());

        page = createPage(SqlQueryExecutionResultPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

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
