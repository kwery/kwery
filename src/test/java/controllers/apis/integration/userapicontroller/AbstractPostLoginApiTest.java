package controllers.apis.integration.userapicontroller;

import controllers.apis.integration.AbstractApiTest;
import dao.UserDao;
import models.User;
import org.junit.Before;
import util.TestUtil;

import static conf.Routes.LOGIN_API;

public abstract class AbstractPostLoginApiTest extends AbstractApiTest {
    protected User loggedInUser;

    @Before
    public void setupPostLoginApiTest() {
        loggedInUser = TestUtil.user();
        getInjector().getInstance(UserDao.class).save(loggedInUser);
        ninjaTestBrowser.postJson(getUrl(LOGIN_API), loggedInUser);
    }
}
