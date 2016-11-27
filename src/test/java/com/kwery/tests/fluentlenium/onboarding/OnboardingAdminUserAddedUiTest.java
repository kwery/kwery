package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.kwery.controllers.apis.OnboardingApiController.ROOT_PASSWORD;
import static com.kwery.controllers.apis.OnboardingApiController.ROOT_USERNAME;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingAdminUserAddedUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected OnboardingUserAddedPage page;

    @Before
    public void setUpOnboardingAdminUserAddedPageTest() throws InterruptedException {
        page = createPage(OnboardingUserAddedPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

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
