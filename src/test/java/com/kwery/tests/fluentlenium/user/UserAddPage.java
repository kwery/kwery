package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class UserAddPage extends FluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";

    @AjaxElement
    @FindBy(className = "add-user-form-f")
    protected FluentWebElement createAdminUserForm;

    @Override
    public String getUrl() {
        return "/#user/add";
    }

    public void submitForm(String... inputs) {
        fill("input").with(inputs);
        click(".user-save-f");
    }

    public void submitForm() {
        fill("input").with();
        click(".user-save-f");
    }

    public String usernameValidationErrorMessage() {
        return $(".username-error-f").getText();
    }

    public String passwordValidationErrorMessage() {
        return $(".password-error-f").getText();
    }

    @Override
    public boolean isRendered() {
        return createAdminUserForm.isDisplayed();
    }

    public void waitForSuccessMessage(User user) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(format(ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername()));
    }

    public void waitForFailureMessage(User user) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername()));
    }
}
