package controllers.apis.integration.userapicontroller;

import controllers.util.TestUtil;
import org.junit.Before;

public abstract class PostLoginApiTest extends UserApiControllerTest {
    @Before
    public void setupPostLoginApiTest() {
        userDao.save(user);
        ninjaTestBrowser.postJson(loginApi, TestUtil.user());
    }
}
