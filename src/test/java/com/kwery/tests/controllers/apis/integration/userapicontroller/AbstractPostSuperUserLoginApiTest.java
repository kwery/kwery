package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;

import static com.kwery.conf.Routes.LOGIN_API;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;

public class AbstractPostSuperUserLoginApiTest extends AbstractApiTest {
    protected User loggedInUser;

    @Before
    public void setupPostLoginApiTest() {
        loggedInUser = TestUtil.user();
        loggedInUser.setSuperUser(true);
        userDbSetUp(loggedInUser);
        ninjaTestBrowser.postJson(getUrl(LOGIN_API), loggedInUser);
    }
}
