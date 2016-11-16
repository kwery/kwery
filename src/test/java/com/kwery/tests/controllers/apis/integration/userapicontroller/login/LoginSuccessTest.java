package com.kwery.tests.controllers.apis.integration.userapicontroller.login;

import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.TestUtil;

import java.io.IOException;

import static com.kwery.conf.Routes.LOGIN_API;
import static java.text.MessageFormat.format;
import static com.kwery.tests.util.Messages.LOGIN_SUCCESS_M;

public class LoginSuccessTest extends AbstractApiTest {
    protected User user;

    @Before
    public void loginSuccessTestSetup() {
        user = TestUtil.user();
        getInjector().getInstance(UserDao.class).save(user);
    }

    @Test
    public void test() throws IOException {
        assertSuccess(
                actionResult(
                    ninjaTestBrowser.postJson(getUrl(LOGIN_API), user)),
                    format(LOGIN_SUCCESS_M, user.getUsername()
                )
        );
    }
}
