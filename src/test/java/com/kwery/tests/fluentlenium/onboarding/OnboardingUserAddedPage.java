package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.security.Unauthenticated;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.concurrent.TimeUnit;

import static com.kwery.tests.util.Messages.ONBOARDING_ROOT_USER_CREATED_M;
import static java.text.MessageFormat.format;

@Unauthenticated
public class OnboardingUserAddedPage extends FluentPage implements RepoDashPage {
    @AjaxElement(timeOutInSeconds = 30)
    @FindBy(className = "f-admin-user-added")
    protected FluentWebElement jumbotron;

    @Override
    public String getUrl() {
        return "/";
    }

    @Override
    public boolean isRendered() {
        await().atMost(30, TimeUnit.SECONDS).until(jumbotron).isDisplayed();
        return true;
    }

    public boolean containsAdminUserCreatedMessage(User user) {
        //TODO - Figure out a way to compare HTML instead of text
        return format(ONBOARDING_ROOT_USER_CREATED_M, user.getUsername(), user.getPassword()).equals($(".f-admin-user-added p").getText());
    }
}
