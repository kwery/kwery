package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static conf.Routes.ONBOARDING_ADD_ADMIN_USER;
import static controllers.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

public class UserControllerTest extends DashRepoNinjaTest {
    private UserDao userDao;

    @Before
    public void before() {
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() throws IOException {
        String username = "purvi";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        String url = getUrl(ONBOARDING_ADD_ADMIN_USER);

        String firstResponse = ninjaTestBrowser.postJson(url, user);
        ActionResult firstResult = new ObjectMapper().readValue(firstResponse, ActionResult.class);

        assertEquals("Returned status is success", success, firstResult.getStatus());
        String userCreatedMessage = String.format("Admin user with user name %s created successfully", username);
        assertEquals("Returned message matches", userCreatedMessage, firstResult.getMessage());

        User userFromDb = userDao.getByUsername(username);

        assertEquals("User created in db with user name", username, userFromDb.getUsername());
        assertEquals("User created in db with password", password, userFromDb.getPassword());
        assertTrue("User created in db has a non zero id", userFromDb.getId() > 0);

        String secondResponse = ninjaTestBrowser.postJson(url, user);
        ActionResult secondResult = new ObjectMapper().readValue(secondResponse, ActionResult.class);

        assertEquals("Returned status is failure", failure, secondResult.getStatus());
        String userExistsMessage = MessageFormat.format(ADMIN_USER_ADDITION_FAILURE_M, username);
        assertEquals("Returned message matches", userExistsMessage, secondResult.getMessage());

        assertTrue("Only one user is present in the db with user name", userDao.getByUsername(username) != null);
    }
}
