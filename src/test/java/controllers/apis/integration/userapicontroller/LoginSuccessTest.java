package controllers.apis.integration.userapicontroller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static controllers.util.Messages.LOGIN_SUCCESS_M;
import static java.text.MessageFormat.format;

public class LoginSuccessTest extends UserApiControllerTest {
    @Before
    public void loginSuccessTestSetup() {
        userDao.save(user);
    }

    @Test
    public void test() throws IOException {
        assertSuccess(actionResult(ninjaTestBrowser.postJson(loginApi, user)), format(LOGIN_SUCCESS_M, user.getUsername()));
    }
}
