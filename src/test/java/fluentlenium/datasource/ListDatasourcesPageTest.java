package fluentlenium.datasource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ListDatasourcesPageTest extends RepoDashFluentLeniumTest {
    protected ListDatasourcesPage page;

    @Before
    public void setUpListDatasourcesPageTest() {
        UserTableUtil userTableUtil = new UserTableUtil();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation(),
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password0", 3306, MYSQL.name(), "foo.com", "user0")
                                .values(2, "testDatasource1", "password1", 3307, MYSQL.name(), "bar.com", "user1")
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

        page = createPage(ListDatasourcesPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list datasources page");
        }
    }

    @Test
    public void test() {
        page.waitForRows(2);
        List<String> headers = page.headers();
        assertThat(headers, hasSize(5));

        assertThat(headers.get(0), is(Messages.LABEL_M));
        assertThat(headers.get(1), is(Messages.URL_M));
        assertThat(headers.get(2), is(Messages.PORT_M));
        assertThat(headers.get(3), is(Messages.USER_NAME_M));
        assertThat(headers.get(4), is(Messages.PASSWORD_M));

        List<List<String>> rows = page.rows();

        assertThat(rows, hasSize(2));

        List<String> first = rows.get(0);

        assertThat(first.get(0), is("testDatasource0"));
        assertThat(first.get(1), is("foo.com"));
        assertThat(first.get(2), is("3306"));
        assertThat(first.get(3), is("user0"));
        assertThat(first.get(4), is("password0"));

        List<String> second = rows.get(1);

        assertThat(second.get(0), is("testDatasource1"));
        assertThat(second.get(1), is("bar.com"));
        assertThat(second.get(2), is("3307"));
        assertThat(second.get(3), is("user1"));
        assertThat(second.get(4), is("password1"));
    }
}
