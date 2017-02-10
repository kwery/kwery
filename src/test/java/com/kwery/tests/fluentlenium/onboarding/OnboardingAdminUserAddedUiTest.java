package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.kwery.controllers.apis.OnboardingApiController.ROOT_PASSWORD;
import static com.kwery.controllers.apis.OnboardingApiController.ROOT_USERNAME;

public class OnboardingAdminUserAddedUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Page
    protected OnboardingUserAddedPage page;

    @Before
    public void setUpOnboardingAdminUserAddedPageTest() throws InterruptedException {
        page.go();
        page.isAt();
    }

    @Test
    public void test() {
        User user = new User();
        user.setUsername(ROOT_USERNAME);
        user.setPassword(ROOT_PASSWORD);

        page.assertAdminUserCreatedMessage(user);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
