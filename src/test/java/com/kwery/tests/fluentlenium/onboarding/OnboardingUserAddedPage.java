package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.security.Unauthenticated;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.Messages.ONBOARDING_ROOT_USER_CREATED_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Unauthenticated
public class OnboardingUserAddedPage extends FluentPage implements RepoDashPage {
    @Wait(timeout = TIMEOUT_SECONDS, timeUnit = SECONDS)
    @FindBy(className = "f-admin-user-added")
    protected FluentWebElement jumbotron;

    @Override
    public String getUrl() {
        return "/";
    }

    @Override
    public boolean isRendered() {
        await().atMost(30, SECONDS).until(jumbotron).displayed();
        return true;
    }

    public boolean containsAdminUserCreatedMessage(User user) {
        //TODO - Figure out a way to compare HTML instead of text
        return format(ONBOARDING_ROOT_USER_CREATED_M, user.getUsername(), user.getPassword()).equals($(".f-admin-user-added p").text());
    }
}
