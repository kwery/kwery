package controllers.apis.integration.userapicontroller;

import controllers.util.TestUtil;
import org.junit.Before;

public abstract class PostLoginApiTest extends UserApiControllerTest {
    @Before
    public void before() {
        super.before();
        userDao.save(user);
        ninjaTestBrowser.postJson(loginApi, TestUtil.user());
    }
}
