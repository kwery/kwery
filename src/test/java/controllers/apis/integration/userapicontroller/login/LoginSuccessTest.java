package controllers.apis.integration.userapicontroller.login;

import controllers.apis.integration.AbstractApiTest;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.TestUtil;

import java.io.IOException;

import static conf.Routes.LOGIN_API;
import static java.text.MessageFormat.format;
import static util.Messages.LOGIN_SUCCESS_M;

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
