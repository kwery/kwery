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
import static com.kwery.views.ActionResult.Status.failure;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserApiControllerEditUserFailureTest extends AbstractPostLoginApiTest {
    private User existing0;
    private User existing1;
    private UserDao userDao;

    @Before
    public void setUp() {
        existing0 = TestUtil.user();
        userDbSetUp(existing0);

        existing1 = TestUtil.user();
        userDbSetUp(existing1);

        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void testNew() {
        User user = userWithoutId();
        user.setEmail(existing0.getEmail());

        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResultStatus(response, failure);

        assertThat(userDao.list(), hasSize(3));
    }

    @Test
    public void testExisting() {
        existing0.setEmail(existing1.getEmail());
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), existing0);
        assertActionResultStatus(response, failure);

        assertThat(userDao.list(), hasSize(3));
    }
}
