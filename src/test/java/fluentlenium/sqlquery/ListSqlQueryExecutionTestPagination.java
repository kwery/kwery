package fluentlenium.sqlquery;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static models.SqlQuery.COLUMN_QUERY;
import static models.SqlQueryExecution.COLUMN_EXECUTION_END;
import static models.SqlQueryExecution.COLUMN_EXECUTION_ID;
import static models.SqlQueryExecution.COLUMN_EXECUTION_START;
import static models.SqlQueryExecution.COLUMN_QUERY_RUN_ID_FK;
import static models.SqlQueryExecution.COLUMN_RESULT;
import static models.SqlQueryExecution.COLUMN_STATUS;
import static models.SqlQueryExecution.Status.FAILURE;
import static models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ListSqlQueryExecutionTestPagination extends RepoDashFluentLeniumTest {
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
                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
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
    public void test() {
        page.fillResultCount(1);
        page.filter();

        page.waitForFilterResult(1);

        assertThat(page.isPreviousEnabled(), is(false));

        page.clickNext();

        page.waitForStatus(FAILURE);

        assertThat(page.isNextEnabled(), is(false));
        assertThat(page.isPreviousEnabled(), is(true));
    }

    @Test
    public void testResetOnResultCountChange() {
        page.fillResultCount(1);
        page.filter();

        page.waitForFilterResult(1);

        assertThat(page.isPreviousEnabled(), is(false));

        page.clickNext();

        page.waitForStatus(FAILURE);

        page.fillResultCount(1);
        page.filter();

        page.waitForStatus(SUCCESS);
        //Page got reset
    }
}
