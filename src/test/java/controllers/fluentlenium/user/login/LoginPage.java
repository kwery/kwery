package controllers.fluentlenium.user.login;

import models.User;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static controllers.fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static controllers.util.Messages.LOGIN_FAILURE_M;
import static controllers.util.Messages.LOGIN_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class LoginPage extends FluentPage {
    @AjaxElement
    @FindBy(id = "loginForm")
    protected FluentWebElement loginForm;

    private String baseUrl;

    @Override
    public String getUrl() {
        return baseUrl + "/#user/login";
    }

    public void submitForm(String... inputs) {
        fill("input").with(inputs);
        click("#login");
    }

    public boolean isRendered() {
        return loginForm.isDisplayed();
    }

    public void waitForSuccessMessage(User user) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_info").hasText(format(LOGIN_SUCCESS_M, user.getUsername()));
    }

    public void waitForFailureMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_error").hasText(LOGIN_FAILURE_M);
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
