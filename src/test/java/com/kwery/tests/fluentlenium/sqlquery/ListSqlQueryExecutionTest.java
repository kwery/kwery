package com.kwery.tests.fluentlenium.sqlquery;

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
import com.kwery.tests.util.Messages;

import java.util.List;
import java.util.UUID;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static com.kwery.tests.fluentlenium.sqlquery.ListSqlQueryExecutionPage.RESULT_TABLE_COLUMN_COUNT;
import static junit.framework.TestCase.fail;
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
import static com.kwery.models.SqlQueryExecution.Status.FAILURE;
import static com.kwery.models.SqlQueryExecution.Status.KILLED;
import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ListSqlQueryExecutionTest extends RepoDashFluentLeniumTest {
    protected ListSqlQueryExecutionPage page;

    @Before
    public void setUpListSqlQueryExecutionTest() {
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
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(2, "* * * * *", "testQuery1", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecution.TABLE)
                                .columns(SqlQueryExecution.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "executionId0", 1475158740747l, "result", SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, "result", SUCCESS, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016

                                .values(11, 1475246507724l, "executionId1", 1475245307680l, null, FAILURE, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(12, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016

                                .values(21, 1475333507680l, "executionId2", 1475331707680l, null, KILLED, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(22, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016

                                .values(31, null, "executionId3", 1475418725084l, null, ONGOING, 1) //Sun Oct 02 20:02:05 IST 2016
                                .values(32, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, 2) //Sun Oct 02 20:02:05 IST 2016
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

        page = createPage(ListSqlQueryExecutionPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void testDefaultPage() {
        pageInvariant();

        List<List<String>> executionList = page.getExecutionList();

        assertThat(executionList, hasSize(4));

        assertSuccessRow(executionList.get(0), 0);
        assertOngoingRow(executionList.get(3), 3);
    }

    @Test
    public void testFilterStatus() {
        pageInvariant();
        page.clickFilter();

        page.fillStatus(SUCCESS, FAILURE);
        page.fillResultCount(1);

        page.filter();

        page.waitForFilterResult(1);

        List<List<String>> executionList = page.getExecutionList();

        assertThat(executionList, hasSize(1));

        assertSuccessRow(executionList.get(0), 0);

        page.clickNext();
        page.waitForStatus(FAILURE);

        pageInvariant();

        executionList = page.getExecutionList();

        assertFailureRow(executionList.get(0), 0);

        page.clickPrevious();
        page.waitForStatus(SUCCESS);

        pageInvariant();

        executionList = page.getExecutionList();
        assertSuccessRow(executionList.get(0), 0);
    }

    @Test
    public void testFilterExecutionStartStartAndEnd() {
        pageInvariant();
        page.clickFilter();

        page.fillExecutionStartStart("29/09/2016 19:48");
        page.fillExecutionStartEnd("30/09/2016 19:52");
        page.fillResultCount(1);

        //Fri Sep 30 19:51:47 IST 2016
        page.filter();
        page.waitForFilterResult(1);

        List<List<String>> executionList = page.getExecutionList();
        assertThat(executionList, hasSize(1));

        assertSuccessRow(executionList.get(0), 0);

        page.clickNext();
        page.waitForStatus(FAILURE);
        pageInvariant();

        executionList = page.getExecutionList();

        assertFailureRow(executionList.get(0), 0);

        page.clickPrevious();
        page.waitForStatus(SUCCESS);

        pageInvariant();

        executionList = page.getExecutionList();

        assertSuccessRow(executionList.get(0), 0);
    }

    @Test
    public void testFilterExecutionEndStartAndEnd() {
        pageInvariant();
        page.clickFilter();
        //Fri Sep 30 20:11:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
        page.fillExecutionEndStart("30/09/2016 20:10");
        page.fillExecutionEndEnd("01/10/2016 20:22");
        page.fillResultCount(1);

        page.filter();

        page.waitForFilterResult(1);

        List<List<String>> executionList = page.getExecutionList();
        assertThat(executionList, hasSize(1));

        assertFailureRow(executionList.get(0), 0);

        page.clickNext();
        page.waitForStatus(KILLED);
        pageInvariant();

        executionList = page.getExecutionList();

        assertKilledRow(executionList.get(0), 0);

        page.clickPrevious();

        page.waitForStatus(FAILURE);
        pageInvariant();
        executionList = page.getExecutionList();

        assertFailureRow(executionList.get(0), 0);
    }

    private void assertSuccessRow(List<String> row, int statusPosition) {
        assertThat(row, hasSize(RESULT_TABLE_COLUMN_COUNT));

        //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
        assertThat(row.get(0), is("Thu Sep 29 2016 19:49"));
        assertThat(row.get(1), is("Thu Sep 29 2016 20:09"));
        assertThat(row.get(2), is(SUCCESS.name()));
        assertThat(page.isStatusLink(statusPosition), is(true));
        assertThat(page.statusLink(statusPosition), is(resultLink("executionId0")));
        assertThat(page.isStatusText(statusPosition), is(false));
    }

    private void assertOngoingRow(List<String> row, int statusPosition) {
        assertThat(row, hasSize(RESULT_TABLE_COLUMN_COUNT));

        //Sun Oct 02 20:02:05 IST 2016
        assertThat(row.get(0), is("Sun Oct 02 2016 20:02"));
        assertThat(row.get(1), is(""));
        assertThat(row.get(2), is(ONGOING.name()));
        assertThat(page.isStatusText(statusPosition), is(true));
        assertThat(page.isStatusLink(statusPosition), is(false));
    }

    private void assertFailureRow(List<String> row, int statusPosition) {
        assertThat(row, hasSize(RESULT_TABLE_COLUMN_COUNT));

        assertThat(row.get(0), is("Fri Sep 30 2016 19:51"));
        assertThat(row.get(1), is("Fri Sep 30 2016 20:11"));
        assertThat(row.get(2), is(FAILURE.name()));
        assertThat(page.isStatusLink(statusPosition), is(false));
        assertThat(page.isStatusText(statusPosition), is(true));
    }

    private void assertKilledRow(List<String> row, int statusPosition) {
        assertThat(row, hasSize(RESULT_TABLE_COLUMN_COUNT));

        //Sat Oct 01 19:51:47 IST 2016
        assertThat(row.get(0), is("Sat Oct 01 2016 19:51"));
        assertThat(row.get(1), is("Sat Oct 01 2016 20:21"));
        assertThat(row.get(2), is(KILLED.name()));
        assertThat(page.isStatusLink(statusPosition), is(false));
        assertThat(page.isStatusText(statusPosition), is(true));
    }

    private String resultLink(String executionId) {
        return getServerAddress() + "/#sql-query/1/execution/" + executionId;
    }

    private void pageInvariant() {
        List<String> headerColumns = page.getExecutionListHeaders();
        assertThat(headerColumns, hasSize(RESULT_TABLE_COLUMN_COUNT));

        assertThat(headerColumns.get(0), is(Messages.START_M));
        assertThat(headerColumns.get(1), is(Messages.END_M));
        assertThat(headerColumns.get(2), is(Messages.STATUS_M));

        assertThat(page.sqlQuery(), is("select * from foo"));
    }
}