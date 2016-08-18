package controllers.routetest;

import conf.Routes;
import org.junit.Test;

public class OnboardingControllerRouteTest extends RouteTest {
    @Test
    public void testWelcome() {
        this.setUrl(Routes.ONBOARDING_WELCOME);
        this.assertGetHtml();
    }

    @Test
    public void testCreateAdminUserHtml() {
        this.setUrl(Routes.ONBOARDING_ADD_ADMIN_USER_HTML);
        this.assertGetHtml();
    }

    @Test
    public void testCreateAdminUserJs() {
        this.setUrl(Routes.ONBOARDING_ADD_ADMIN_USER_JS);
        this.assertGetJs();
    }
}
