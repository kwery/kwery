package fluentlenium.onboarding;

import fluentlenium.RepoDashFluentLeniumTest;
import models.User;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static controllers.apis.OnboardingApiController.ROOT_PASSWORD;
import static controllers.apis.OnboardingApiController.ROOT_USERNAME;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingAdminUserAddedPageTest extends RepoDashFluentLeniumTest {
    protected OnboardingAdminUserAddedPage page;

    @Before
    public void setUpOnboardingAdminUserAddedPageTest() throws InterruptedException {
        page = createPage(OnboardingAdminUserAddedPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        //TODO - Fix this, sleep should not be required here
        TimeUnit.SECONDS.sleep(10);

        if (!page.isRendered()) {
            fail("Could not render admin user added onboarding page");
        }
    }

    @Test
    public void test() {
        User user = new User();
        user.setUsername(ROOT_USERNAME);
        user.setPassword(ROOT_PASSWORD);

        assertThat(page.containsAdminUserCreatedMessage(user), is(true));
    }
}
