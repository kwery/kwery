package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.security.Unauthenticated;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;

import static com.kwery.tests.util.Messages.ONBOARDING_ROOT_USER_CREATED_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

@Unauthenticated
@PageUrl("/")
public class OnboardingUserAddedPage extends FluentPage {
    @Override
    public void isAt() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".welcome-f")).displayed();
    }

    public void waitForAdminUserCreatedMessage(User user) {
        String userCreatedMessage = format(ONBOARDING_ROOT_USER_CREATED_M, user.getUsername(), user.getPassword());
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".user-added-f p", withText(userCreatedMessage))).displayed();
    }
}
