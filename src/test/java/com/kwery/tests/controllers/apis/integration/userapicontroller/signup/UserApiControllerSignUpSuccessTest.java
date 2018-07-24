package com.kwery.tests.controllers.apis.integration.userapicontroller.signup;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.assertActionResultStatus;
import static com.kwery.tests.util.TestUtil.userWithoutId;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UserApiControllerSignUpSuccessTest extends AbstractApiTest {
    protected boolean firstUser;

    public UserApiControllerSignUpSuccessTest(boolean firstUser) {
        this.firstUser = firstUser;
    }

    @Parameters(name = "firstUser{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true}, {false}
        });
    }

    private UserDao userDao;

    @Before
    public void setUp() {
        if (!firstUser) {
            userDbSetUp(TestUtil.user());
        }
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() {
        User user = userWithoutId();
        user.setSuperUser(null);

        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), user);
        assertActionResultStatus(response, success);
        if (!firstUser) {
            assertThat(userDao.getUserByEmail(user.getEmail()).getSuperUser(), nullValue());
        } else {
            assertThat(userDao.getUserByEmail(user.getEmail()).getSuperUser(), is(true));
        }
    }
}
