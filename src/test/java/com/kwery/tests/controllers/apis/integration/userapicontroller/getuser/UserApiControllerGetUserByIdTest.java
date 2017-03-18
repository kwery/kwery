package com.kwery.tests.controllers.apis.integration.userapicontroller.getuser;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.UserApiController;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.assertUser;

public class UserApiControllerGetUserByIdTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                UserApiController.class,
                "userById",
                ImmutableMap.of(
                        "userId", loggedInUser.getId()
                )
        );

        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        assertUser(response, loggedInUser);
    }
}

