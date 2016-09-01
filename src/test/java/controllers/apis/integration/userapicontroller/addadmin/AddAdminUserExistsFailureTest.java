package controllers.apis.integration.userapicontroller.addadmin;

import controllers.apis.integration.AbstractApiTest;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.TestUtil;

import java.io.IOException;

import static conf.Routes.ADD_ADMIN_USER_API;
import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static util.Messages.ADMIN_USER_ADDITION_FAILURE_M;

public class AddAdminUserExistsFailureTest extends AbstractApiTest {
    private UserDao userDao;
    protected User user;

    @Before
    public void addAdminUserExistsFailureTestSetup() {
        userDao = getInjector().getInstance(UserDao.class);
        user = TestUtil.user();
        userDao.save(user);
    }

    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(ADD_ADMIN_USER_API), user)),
                format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername())
        );

        assertThat(userDao.getByUsername(user.getUsername()), notNullValue());
    }
}
