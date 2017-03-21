package com.kwery.tests.controllers.apis.integration.userapicontroller.edit;

import com.kwery.controllers.apis.UserApiController;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(Parameterized.class)
public class UserApiControllerSignUpWithSuperUserTest extends AbstractPostLoginApiTest {
    protected boolean superUserInDb;
    protected Boolean superUserInRequest;
    private User user;

    public UserApiControllerSignUpWithSuperUserTest(boolean superUserInDb, Boolean superUserInRequest) {
        this.superUserInDb = superUserInDb;
        this.superUserInRequest = superUserInRequest;
    }


    @Parameters(name = "superUserInDb{0}superUserInRequest{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true, true},
                {false, false},
                {true, null},
                {false, null}
        });
    }

    private UserDao userDao;

    @Before
    public void setUp() {
        user = TestUtil.user();
        user.setSuperUser(superUserInDb);
        userDbSetUp(user);
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() {
        User toUpdate = userWithoutId();
        toUpdate.setId(user.getId());
        toUpdate.setSuperUser(superUserInRequest);

        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "signUp");
        String response = ninjaTestBrowser.postJson(getUrl(url), toUpdate);
        assertActionResultStatus(response, success);

        if (superUserInRequest == null) {
            assertThat(userDao.getUserByEmail(toUpdate.getEmail()).getSuperUser(), is(superUserInDb));
        } else {
            assertThat(userDao.getUserByEmail(toUpdate.getEmail()).getSuperUser(), is(superUserInRequest));
        }
    }
}
