package com.kwery.tests.fluentlenium.user;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import static junit.framework.TestCase.fail;
import static org.openqa.selenium.By.className;

public class UserLogoutUiTest extends RepoDashFluentLeniumTest {
    @Before
    public void setUpLogoutFlowTest() {
        UserTableUtil userTableUtil = new UserTableUtil();
        DataSource datasource = DbUtil.getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        userTableUtil.insertOperation()
                )
        );

        dbSetup.launch();

        UserLoginPage loginPage = createPage(UserLoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }
        loginPage.submitForm(userTableUtil.firstRow().getUsername(), userTableUtil.firstRow().getPassword());
        loginPage.waitForSuccessMessage(userTableUtil.firstRow());

        goTo(getServerAddress() + "/");

        await().atMost(TIMEOUT_SECONDS).until(".f-navbar").isDisplayed();
    }

    @Test
    public void test() {
        $(className("f-username")).click();
        await().atMost(TIMEOUT_SECONDS).until(".f-logout").isDisplayed();
        $(className("f-logout")).click();
        await().atMost(TIMEOUT_SECONDS).until(".f-next-steps").isDisplayed();
    }
}
