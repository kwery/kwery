package controllers.modules;

import conf.Routes;
import org.junit.Test;

public class OnboardingModuleControllerTest extends ModuleControllerTest {
    @Test
    public void testWelcome() {
        this.setUrl(Routes.ONBOARDING_WELCOME);
        this.assertGetHtml();
    }

    @Test
    public void testCreateAdminUserHtml() {
        this.setUrl(Routes.ADD_ADMIN_USER_HTML);
        this.assertGetHtml();
    }

    @Test
    public void testCreateAdminUserJs() {
        this.setUrl(Routes.ADD_ADMIN_USER_JS);
        this.assertGetJs();
    }
}
