package fluentlenium.sqlquery;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
import models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import util.Messages;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ListSqlQueriesPageTest extends RepoDashFluentLeniumTest {
    protected ListSqlQueriesPage page;

    @Before
    public void setUpListSqlQueriesPageTest() {
        UserTableUtil userTableUtil = new UserTableUtil();
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation(),
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

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }
        loginPage.submitForm(userTableUtil.firstRow().getUsername(), userTableUtil.firstRow().getPassword());
        loginPage.waitForSuccessMessage(userTableUtil.firstRow());

        page = createPage(ListSqlQueriesPage.class);
        page.withDefaultUrl(getServerAddress());
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
