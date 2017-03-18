package com.kwery.tests.controllers.apis.integration.userapicontroller.edit;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.assertActionResultStatus;
import static com.kwery.tests.util.TestUtil.userWithoutId;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserApiControllerEditUserSuccessTest extends AbstractPostLoginApiTest {
    private User existing;
    private UserDao userDao;

    @Before
    public void setUp() {
        existing = TestUtil.user();
        userDbSetUp(existing);
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void testWithDifferentEmail() {
        User user = userWithoutId();
        user.setId(existing.getId());
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResultStatus(response, success);
        assertThat(userDao.list(), hasSize(2));
    }

    @Test
    public void testWithSameEmailAddress() {
        User user = userWithoutId();
        user.setId(existing.getId());
        user.setEmail(user.getEmail());
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResultStatus(response, success);
        assertThat(userDao.list(), hasSize(2));
    }
}
