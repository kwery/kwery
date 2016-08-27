package controllers.apis.integration.userapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import models.User;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static controllers.util.Messages.PASSWORD_VALIDATION_M;
import static controllers.util.Messages.USERNAME_VALIDATION_M;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AddAdminUserValidationFailureTest extends UserApiControllerTest {

    public static final Map<String, List<String>> EXPECTED_MESSAGE_MAP = ImmutableMap.of(
            "username", ImmutableList.of(USERNAME_VALIDATION_M),
            "password", ImmutableList.of(PASSWORD_VALIDATION_M)
    );

    @Test
    public void testMinimumLength() throws IOException {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("");

        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(addAdminUserApi, invalidUser));

        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());
    }

    @Test
    public void testNull() throws IOException {
        User invalidUser = new User();

        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(addAdminUserApi, invalidUser));

        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());
    }
}
