package com.kwery.tests.fluentlenium.user.login;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.Messages.LOGIN_FAILURE_M;
import static com.kwery.tests.util.Messages.LOGIN_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/login")
public class UserLoginPage extends KweryFluentPage implements RepoDashPage {
    @Wait(timeout = TIMEOUT_SECONDS, timeUnit = SECONDS)
    @FindBy(id = "loginForm")
    protected FluentWebElement loginForm;

    public void submitForm(String... inputs) {
        $("input").fill().with(inputs);
        $("#login").click();
    }

    @Override
    public boolean isRendered() {
        return loginForm.displayed();
    }

    public void waitForSuccessMessage(String username) {
        super.waitForSuccessMessage(LOGIN_SUCCESS_M);
    }

    public void waitForFailureMessage() {
        super.waitForFailureMessage(LOGIN_FAILURE_M);
    }
}
