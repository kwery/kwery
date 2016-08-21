package controllers.apitest;

import conf.Routes;
import controllers.util.TestUtil;
import dao.UserDao;
import org.junit.Before;

public class PostLoginApiTest extends ApiTest {
    @Before
    public void before() {
        getInjector().getInstance(UserDao.class).save(TestUtil.user());
        login();
    }

    protected void login() {
        ninjaTestBrowser.postJson(getUrl(Routes.LOGIN_API), TestUtil.user());
    }
}
