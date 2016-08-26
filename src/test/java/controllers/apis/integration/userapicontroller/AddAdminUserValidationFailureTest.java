package controllers.apis.integration.userapicontroller;

import models.User;
import org.junit.Test;

import java.io.IOException;

import static controllers.util.Messages.PASSWORD_VALIDATION_M;
import static controllers.util.Messages.USERNAME_VALIDATION_M;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AddAdminUserValidationFailureTest extends UserApiControllerTest {
    @Test
    public void testMinimumLength() throws IOException {
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setPassword("");

        assertFailure(
                actionResult(ninjaTestBrowser.postJson(addAdminUserApi, invalidUser)),
                USERNAME_VALIDATION_M, PASSWORD_VALIDATION_M
        );

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());
    }

    @Test
    public void testNull() throws IOException {
        User invalidUser = new User();

        assertFailure(
                actionResult(ninjaTestBrowser.postJson(addAdminUserApi, invalidUser)),
                USERNAME_VALIDATION_M, PASSWORD_VALIDATION_M
        );

        assertThat(userDao.getByUsername(invalidUser.getUsername()), nullValue());
    }
}
