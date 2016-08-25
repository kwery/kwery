package controllers.apis.integration.userapicontroller;

import controllers.util.Messages;
import models.User;
import org.junit.Test;

import java.io.IOException;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AddAdminSuccessTest extends UserApiControllerTest {
    @Test
    public void test() throws IOException {
        assertSuccess(
                actionResult(ninjaTestBrowser.postJson(addAdminUserApi, user)),
                format(Messages.ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername())
        );

        User userFromDb = userDao.getByUsername(user.getUsername());

        assertThat(user.getUsername(), is(userFromDb.getUsername()));
        assertThat(user.getPassword(), is(userFromDb.getPassword()));
        assertThat(userFromDb.getId(), greaterThan(0));
    }
}
