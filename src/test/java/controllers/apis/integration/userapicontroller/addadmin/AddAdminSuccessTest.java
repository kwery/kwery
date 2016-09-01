package controllers.apis.integration.userapicontroller.addadmin;

import conf.Routes;
import controllers.apis.integration.AbstractApiTest;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.Messages;
import util.TestUtil;

import java.io.IOException;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AddAdminSuccessTest extends AbstractApiTest {
    protected UserDao userDao;

    @Before
    public void setupAddAdminSuccessTest() {
        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() throws IOException {
        User user = TestUtil.user();

        assertSuccess(
                actionResult(ninjaTestBrowser.postJson(getUrl(Routes.ADD_ADMIN_USER_API), user)),
                format(Messages.ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername())
        );

        User userFromDb = userDao.getByUsername(user.getUsername());

        assertThat(user.getUsername(), is(userFromDb.getUsername()));
        assertThat(user.getPassword(), is(userFromDb.getPassword()));
        assertThat(userFromDb.getId(), greaterThan(0));
    }
}
