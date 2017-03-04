package com.kwery.tests.controllers.apis.integration.userapicontroller.getuser;

import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.USER;
import static com.kwery.tests.util.TestUtil.assertUser;

public class GetUserTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws IOException {
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(USER));
        assertUser(response, loggedInUser);
    }
}
