package com.kwery.tests.controllers.apis.integration.userapicontroller.signup;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.assertActionResultStatus;
import static com.kwery.tests.util.TestUtil.userWithoutId;
import static com.kwery.views.ActionResult.Status.failure;

public class UserApiControllerSignUpFailureTest extends AbstractApiTest {
    private User existingUser;

    @Before
    public void setUp() {
        existingUser = TestUtil.user();
        userDbSetUp(existingUser);
    }

    @Test
    public void test() {
        User user = userWithoutId();
        user.setEmail(existingUser.getEmail());
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResultStatus(response, failure);
    }
}
