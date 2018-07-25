package com.kwery.tests.fluentlenium.user.save.signup;

import com.kwery.dao.UserDao;
import com.kwery.tests.fluentlenium.user.save.UserSavePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;

import static com.kwery.controllers.apis.OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY;
import static com.kwery.controllers.apis.OnboardingApiController.TEST_ONBOARDING_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class AbstractUserSignUpSetUpUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected boolean onboardingFlow = false;

    @Page
    protected UserSavePage page;

    protected UserDao userDao;

    @Before
    public void setUp() {
        if (this.onboardingFlow) {
            System.setProperty(TEST_ONBOARDING_SYSTEM_KEY, TEST_ONBOARDING_VALUE);
            page.setOnboardingFlow(true);
        } else {
            page.setOnboardingFlow(false);
        }
        page.go();
        userDao = ninjaServerRule.getInjector().getInstance(UserDao.class);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    protected void assertEmptyUsersTable() {
        assertThat(ninjaServerRule.getInjector().getInstance(UserDao.class).list(), hasSize(0));
    }

    public void setOnboardingFlow(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }
}
