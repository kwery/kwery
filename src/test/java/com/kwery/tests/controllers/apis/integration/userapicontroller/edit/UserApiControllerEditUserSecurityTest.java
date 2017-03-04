package com.kwery.tests.controllers.apis.integration.userapicontroller.edit;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.USER_NOT_LOGGED_IN_M;
import static com.kwery.tests.util.TestUtil.assertActionResult;
import static com.kwery.views.ActionResult.Status.failure;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserApiControllerEditUserSecurityTest extends AbstractApiTest {
    private User user;
    private UserDao userDao;

    @Before
    public void setUp() {
        user = TestUtil.user();
        userDbSetUp(user);
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void testNew() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResult(response, new ActionResult(failure, USER_NOT_LOGGED_IN_M));
        assertThat(userDao.list(), hasSize(1));
    }
}
