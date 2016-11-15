package controllers.apis.integration.userapicontroller.addadmin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static conf.Routes.ADD_ADMIN_USER_API;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static util.Messages.PASSWORD_VALIDATION_M;
import static util.Messages.USERNAME_VALIDATION_M;

public class AddAdminUserValidationFailureTest extends AbstractPostLoginApiTest {
    protected UserDao userDao;

    protected static final Map<String, List<String>> EXPECTED_MESSAGE_MAP = ImmutableMap.of(
            "username", ImmutableList.of(USERNAME_VALIDATION_M),
            "password", ImmutableList.of(PASSWORD_VALIDATION_M)
    );

    @Before
    public void setupAddAdminUserValidationFailureTest() {
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void testMinimumLength() throws IOException {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("");

        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_ADMIN_USER_API), invalidUser));

        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());
    }

    @Test
    public void testNull() throws IOException {
        User invalidUser = new User();

        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_ADMIN_USER_API), invalidUser));

        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());
    }
}
