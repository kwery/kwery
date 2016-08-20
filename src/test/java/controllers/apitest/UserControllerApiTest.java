package controllers.apitest;

import controllers.util.Messages;
import controllers.util.TestUtil;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static conf.Routes.ONBOARDING_ADD_ADMIN_USER;
import static controllers.UserController.ONBOARDING_POST_ADMIN_USER_CREATION_ACTION;
import static controllers.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static controllers.util.Messages.ADMIN_USER_ADDITION_NEXT_STEP_M;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserControllerApiTest extends ApiTest {
    private UserDao userDao;

    @Before
    public void before() {
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() throws IOException {
        User user = TestUtil.user();

        String url = getUrl(ONBOARDING_ADD_ADMIN_USER);

        assertSuccessNextAction(
                actionResult(ninjaTestBrowser.postJson(url, user)),
                format(Messages.ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername()),
                ADMIN_USER_ADDITION_NEXT_STEP_M,
                ONBOARDING_POST_ADMIN_USER_CREATION_ACTION
        );

        User userFromDb = userDao.getByUsername(user.getUsername());

        assertEquals("User created in db with user name", user.getUsername(), userFromDb.getUsername());
        assertEquals("User created in db with password", user.getPassword(), userFromDb.getPassword());
        assertTrue("User created in db has a non zero id", userFromDb.getId() > 0);

        String userExistsMessage = format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername());
        assertFailure(actionResult(ninjaTestBrowser.postJson(url, user)), userExistsMessage);

        assertTrue("Only one user is present in the db with user name", userDao.getByUsername(user.getUsername()) != null);
    }
}
