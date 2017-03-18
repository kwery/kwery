package com.kwery.tests.controllers.apis.integration.userapicontroller.login;

import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.LOGIN_API;
import static com.kwery.tests.util.Messages.LOGIN_FAILURE_M;

public class LoginFailureTest extends AbstractApiTest {
    @Test
    public void test() throws IOException {
        assertFailure(actionResult(ninjaTestBrowser.postJson(getUrl(LOGIN_API), TestUtil.user())), LOGIN_FAILURE_M);
    }
}
