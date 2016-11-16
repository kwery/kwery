package com.kwery.tests.fluentlenium.user.login;

import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.models.User;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static com.kwery.tests.util.Messages.LOGIN_FAILURE_M;
import static com.kwery.tests.util.Messages.LOGIN_SUCCESS_M;

public class LoginPage extends FluentPage implements RepoDashPage {
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
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".f-success-message").hasText(format(LOGIN_SUCCESS_M, user.getUsername()));
    }

    public void waitForSuccessMessage(String username) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".f-success-message").hasText(format(LOGIN_SUCCESS_M, username));
    }

    public void waitForFailureMessage() {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".f-failure-message").hasText(LOGIN_FAILURE_M);
    }
}
