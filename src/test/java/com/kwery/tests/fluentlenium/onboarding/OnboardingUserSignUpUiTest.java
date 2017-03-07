package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import org.awaitility.Awaitility;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.hook.wait.Wait;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
public class OnboardingUserSignUpUiTest extends ChromeFluentTest {
    static {
        System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
    }

    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected ActionResultComponent actionResultComponent;

    @Before
    public void setUp() {
        goTo("/");
    }

    @Test
    public void test() {
        Awaitility.await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(ninjaServerRule.getServerUrl() + "/#user/sign-up?onboarding=true"));
        actionResultComponent.assertInfoMessage(Messages.ONBOARDING_USER_ADD_M);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
