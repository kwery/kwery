package com.kwery.tests.controllers.apis.integration.userapicontroller.login;

import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.models.User;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.LOGIN_API;
import static com.kwery.tests.util.Messages.LOGIN_FAILURE_M;

public class LoginFailureTest extends AbstractApiTest {
    @Test
    public void test() throws IOException {
        User notInDb = new User();
        notInDb.setUsername("foo");
        notInDb.setPassword("moo");
        assertFailure(actionResult(ninjaTestBrowser.postJson(getUrl(LOGIN_API), notInDb)), LOGIN_FAILURE_M);
    }
}
