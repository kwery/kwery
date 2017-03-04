package com.kwery.tests.controllers.apis.integration.userapicontroller.signup;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import ninja.Router;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.assertActionResultStatus;
import static com.kwery.tests.util.TestUtil.userWithoutId;
import static com.kwery.views.ActionResult.Status.success;

public class UserApiControllerSignUpSuccessTest extends AbstractApiTest {
    @Test
    public void test() {
        User user = userWithoutId();
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResultStatus(response, success);
    }
}
