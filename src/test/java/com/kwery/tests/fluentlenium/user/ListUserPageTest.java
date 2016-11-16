package com.kwery.tests.fluentlenium.user;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.CompositeOperation;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.User;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.fluentlenium.user.ListUsersPage.COLUMNS;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static com.kwery.tests.util.Messages.DELETE_M;
import static com.kwery.tests.util.Messages.PASSWORD_M;
import static com.kwery.tests.util.Messages.USER_NAME_M;

public class ListUserPageTest extends RepoDashFluentLeniumTest {
    protected UserTableUtil userTableUtil;
    protected ListUsersPage page;

    @Before
    public void before() {
        userTableUtil = new UserTableUtil(2);

        DbSetup dbSetup = new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                CompositeOperation.sequenceOf(
                        userTableUtil.insertOperation()
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

        page = createPage(ListUsersPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render login page");
        }
    }

    @Test
    public void test() {
        page.waitForRows(2);

        List<String> headers = page.headers();

        assertThat(headers, hasSize(COLUMNS));

        assertThat(headers.get(0), is(USER_NAME_M));
        assertThat(headers.get(1), is(PASSWORD_M));
        assertThat(headers.get(2), is(DELETE_M));

        List<List<String>> rows = page.rows();

        assertThat(rows, hasSize(2));

        List<String> firstRow = rows.get(0);
        User user0 = userTableUtil.row(0);
        assertThat(firstRow.get(0), is(user0.getUsername()));
        assertThat(firstRow.get(1), is(user0.getPassword()));

        List<String> secondRow = rows.get(1);
        User user1 = userTableUtil.row(1);
        assertThat(secondRow.get(0), is(user1.getUsername()));
        assertThat(secondRow.get(1), is(user1.getPassword()));
    }
}
