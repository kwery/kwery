package fluentlenium.sqlquery;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Test;
import util.Messages;

import javax.sql.DataSource;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;
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
import static models.SqlQueryExecution.Status.ONGOING;
import static models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static util.Messages.DATASOURCE_M;
import static util.Messages.KILL_QUERY_M;
import static util.Messages.QUERY_M;
import static util.Messages.START_M;

public class ListExecutingSqlQueriesTest extends RepoDashFluentLeniumTest {
    protected ListExecutingSqlQueriesPage page;

    @Before
    public void setUpListOngoingQueriesTest() {
        UserTableUtil userTableUtil = new UserTableUtil();
        DataSource datasource = DbUtil.getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        userTableUtil.insertOperation(),
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

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }
        loginPage.submitForm(userTableUtil.firstRow().getUsername(), userTableUtil.firstRow().getPassword());
        loginPage.waitForSuccessMessage(userTableUtil.firstRow());

        page = createPage(ListExecutingSqlQueriesPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
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
