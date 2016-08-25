package controllers.apis.integration.userapicontroller;

import models.User;
import org.junit.Test;

import java.io.IOException;

import static controllers.util.Messages.LOGIN_FAILURE_M;

public class LoginFailureTest extends UserApiControllerTest {
    @Test
    public void test() throws IOException {
        User notInDb = new User();
        user.setUsername("foo");
        user.setPassword("moo");

        assertFailure(actionResult(ninjaTestBrowser.postJson(loginApi, notInDb)), LOGIN_FAILURE_M);
    }
}
