package com.kwery.tests.controllers.apis.integration.userapicontroller.login;

import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.LOGIN_API;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.LOGIN_SUCCESS_M;

public class LoginSuccessTest extends AbstractApiTest {
    protected User user;

    @Before
    public void loginSuccessTestSetup() {
        user = TestUtil.user();
        userDbSetUp(user);
    }

    @Test
    public void test() throws IOException {
        assertSuccess(actionResult(ninjaTestBrowser.postJson(getUrl(LOGIN_API), user)), LOGIN_SUCCESS_M);
    }
}
