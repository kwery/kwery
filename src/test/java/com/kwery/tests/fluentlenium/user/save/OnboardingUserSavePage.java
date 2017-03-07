package com.kwery.tests.fluentlenium.user.save;

import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/sign-up?onboarding=true")
public class OnboardingUserSavePage extends UserSavePage {
}
