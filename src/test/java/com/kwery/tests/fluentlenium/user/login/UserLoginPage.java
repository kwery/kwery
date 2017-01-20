package com.kwery.tests.fluentlenium.user.login;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.Messages.LOGIN_FAILURE_M;
import static com.kwery.tests.util.Messages.LOGIN_SUCCESS_M;
import static java.text.MessageFormat.format;

public class UserLoginPage extends KweryFluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "loginForm")
    protected FluentWebElement loginForm;

    @Override
    public String getUrl() {
        return "/#user/login";
    }

    public void submitForm(String... inputs) {
        fill("input").with(inputs);
        click("#login");
    }

    @Override
    public boolean isRendered() {
        return loginForm.isDisplayed();
    }

    public void waitForSuccessMessage(User user) {
        super.waitForSuccessMessage(format(LOGIN_SUCCESS_M, user.getUsername()));
    }

    public void waitForSuccessMessage(String username) {
        super.waitForSuccessMessage(format(LOGIN_SUCCESS_M, username));
    }

    public void waitForFailureMessage() {
        super.waitForFailureMessage(LOGIN_FAILURE_M);
    }
}
