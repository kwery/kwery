package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import org.junit.Before;
import com.kwery.tests.util.TestUtil;

import static com.kwery.conf.Routes.LOGIN_API;

public abstract class AbstractPostLoginApiTest extends AbstractApiTest {
    protected User loggedInUser;

    @Before
    public void setupPostLoginApiTest() {
        loggedInUser = TestUtil.user();
        getInjector().getInstance(UserDao.class).save(loggedInUser);
        ninjaTestBrowser.postJson(getUrl(LOGIN_API), loggedInUser);
    }
}
