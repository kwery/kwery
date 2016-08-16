package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Routes;
import dao.UserDao;
import models.User;
import ninja.NinjaTest;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserControllerTest extends NinjaTest {
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

        String url = getUrl(Routes.ONBOARDING_CREATE_ADMIN_USER);

        String firstResponse = ninjaTestBrowser.postJson(url, user);
        ActionResult firstResult = new ObjectMapper().readValue(firstResponse, ActionResult.class);

        assertEquals("Returned status is success", ActionResult.Status.success, firstResult.getStatus());
        assertEquals("Returned message matches", String.format("Admin user with user name %s created successfully", username), firstResult.getMessage());

        User userFromDb = userDao.getByUsername(username);

        assertEquals("User created in db with user name", username, userFromDb.getUsername());
        assertEquals("User created in db with password", password, userFromDb.getPassword());
        assertTrue("User created in db has a non zero id", userFromDb.getId() > 0);

        String secondResponse = ninjaTestBrowser.postJson(url, user);
        ActionResult secondResult = new ObjectMapper().readValue(secondResponse, ActionResult.class);

        assertEquals("Returned status is failure", ActionResult.Status.failure, secondResult.getStatus());
        assertEquals("Returned message matches",
                String.format("An admin user with user name %s already exists, please choose a different username", username), secondResult.getMessage());

        assertTrue("Only one user is present in the db with user name", userDao.getByUsername(username) != null);
    }

    private String getUrl(String path) {
        String a = getServerAddress();
        a = a.substring(0, a.length() - 1);
        return a + path;
    }
}
