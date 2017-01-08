package com.kwery.tests.controllers.apis.integration.userapicontroller.addadmin;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.conf.Routes.ADD_ADMIN_USER_API;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertThat;

public class AddAdminUserExistsFailureTest extends AbstractPostLoginApiTest {
    private UserDao userDao;
    User user;

    @Before
    public void addAdminUserExistsFailureTestSetup() {
        user = TestUtil.user();
        userDbSetUp(user);

        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() throws IOException {
        user.setId(null);
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(ADD_ADMIN_USER_API), user)),
                format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername())
        );
        assertThat(userDao.list(), hasSize(2)); //Including logged in user
    }
}
