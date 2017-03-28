package com.kwery.tests.controllers.apis.integration.userapicontroller.edit;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.dao.UserDao;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.assertActionResultStatus;
import static com.kwery.views.ActionResult.Status.failure;

public class UserApiControllerDemoteSuperUserFailureTest extends AbstractPostLoginApiTest {
    @Before
    public void setUp() {
        super.loggedInUser.setSuperUser(true);
        getInjector().getInstance(UserDao.class).update(super.loggedInUser);
    }

    @Test
    public void test() {
        loggedInUser.setSuperUser(false);
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), loggedInUser);
        assertActionResultStatus(response, failure);
    }
}
