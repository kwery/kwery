package com.kwery.tests.fluentlenium.datasource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.Datasource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static com.kwery.tests.fluentlenium.datasource.DatasourceListPage.COLUMNS;
import static junit.framework.TestCase.fail;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.DELETE_M;
import static com.kwery.tests.util.Messages.LABEL_M;
import static com.kwery.tests.util.Messages.PASSWORD_M;
import static com.kwery.tests.util.Messages.PORT_M;
import static com.kwery.tests.util.Messages.URL_M;
import static com.kwery.tests.util.Messages.USER_NAME_M;

public class DatasourceListUiTest extends RepoDashFluentLeniumTest {
    protected DatasourceListPage page;

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

        page = createPage(DatasourceListPage.class);
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
        assertThat(headers, hasSize(COLUMNS));

        assertThat(headers.get(0), is(LABEL_M));
        assertThat(headers.get(1), is(URL_M));
        assertThat(headers.get(2), is(PORT_M));
        assertThat(headers.get(3), is(USER_NAME_M));
        assertThat(headers.get(4), is(PASSWORD_M));
        assertThat(headers.get(5), is(DELETE_M));

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
