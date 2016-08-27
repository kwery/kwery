package controllers.apis.integration.userapicontroller;

import controllers.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static controllers.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AddAdminUserExistsFailureTest extends UserApiControllerTest {
    @Before
    public void before() {
        super.before();
        userDao.save(TestUtil.user());
    }

    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(addAdminUserApi, user)),
                format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername())
        );

        assertThat(userDao.getByUsername(user.getUsername()), notNullValue());
    }
}
