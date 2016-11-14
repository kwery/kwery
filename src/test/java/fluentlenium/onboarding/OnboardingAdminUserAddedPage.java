package fluentlenium.onboarding;

import fluentlenium.RepoDashPage;
import models.User;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static java.text.MessageFormat.format;
import static util.Messages.ONBOARDING_ROOT_USER_CREATED_M;

public class OnboardingAdminUserAddedPage extends FluentPage implements RepoDashPage {
    @AjaxElement(timeOutInSeconds = 30)
    @FindBy(className = "f-admin-user-added")
    protected FluentWebElement jumbotron;

    @Override
    public String getUrl() {
        return "/";
    }

    @Override
    public boolean isRendered() {
        return jumbotron.isDisplayed();
    }

    public boolean containsAdminUserCreatedMessage(User user) {
        //TODO - Figure out a way to compare HTML instead of text
        return format(ONBOARDING_ROOT_USER_CREATED_M, user.getUsername(), user.getPassword()).equals($(".f-admin-user-added p").getText());
    }
}
