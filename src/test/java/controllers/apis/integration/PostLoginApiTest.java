package controllers.apis.integration;

import conf.Routes;
import controllers.util.TestUtil;
import dao.UserDao;
import models.User;
import org.junit.Before;

public class PostLoginApiTest extends ApiTest {
    protected User savedUser;
    protected UserDao userDao;

    @Before
    public void before() {
        savedUser = TestUtil.user();
        userDao = getInjector().getInstance(UserDao.class);
        userDao.save(savedUser);
        login();
    }

    protected void login() {
        ninjaTestBrowser.postJson(getUrl(Routes.LOGIN_API), TestUtil.user());
    }
}
