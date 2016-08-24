package controllers.modules;

import controllers.util.TestUtil;
import org.junit.Test;

import static conf.Routes.LOGIN_API;
import static conf.Routes.LOGIN_COMPONENT_HTML;
import static conf.Routes.ADD_ADMIN_USER_API;

public class UserModuleControllerTest extends ModuleControllerTest {
    @Test
    public void testCreateAdminUser() {
        this.setUrl(ADD_ADMIN_USER_API);
        this.setPostParams(TestUtil.userParams());
        this.assertPostJson();
    }

    @Test
    public void testLogin() {
        this.setUrl(LOGIN_API);
        this.setPostParams(TestUtil.userParams());
        this.assertPostJson();
    }

    @Test
    public void testLoginHtml() {
        this.setUrl(LOGIN_COMPONENT_HTML);
        this.assertGetHtml();
    }
}
