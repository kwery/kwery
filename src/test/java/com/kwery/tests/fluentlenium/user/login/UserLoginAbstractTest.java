package com.kwery.tests.fluentlenium.user.login;

import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.models.User;
import org.junit.Before;

import static com.kwery.tests.util.TestUtil.user;

public abstract class UserLoginAbstractTest extends RepoDashFluentLeniumTest {
    protected UserLoginPage page;
    protected User user;

    @Before
    public void setUpLoginTest() {
        page = createPage(UserLoginPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        page.isRendered();
        user = user();
    }
}
