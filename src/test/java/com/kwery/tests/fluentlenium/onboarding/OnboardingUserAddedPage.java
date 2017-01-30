package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.security.Unauthenticated;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.Messages.ONBOARDING_ROOT_USER_CREATED_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

@Unauthenticated
@PageUrl("/")
@Wait(timeout = TIMEOUT_SECONDS, timeUnit = SECONDS)
public class OnboardingUserAddedPage extends FluentPage {
    @Override
    public void isAt() {
        assertThat(el(".welcome-f")).isDisplayed();
    }

    public void assertAdminUserCreatedMessage(User user) {
        String userCreatedMessage = format(ONBOARDING_ROOT_USER_CREATED_M, user.getUsername(), user.getPassword());
        assertThat(el(".user-added-f p", withText(userCreatedMessage))).isDisplayed();
    }
}
