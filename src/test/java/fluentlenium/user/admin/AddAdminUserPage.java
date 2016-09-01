package fluentlenium.user.admin;

import fluentlenium.RepoDashFluentLeniumTest;
import models.User;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static util.Messages.ADMIN_USER_ADDITION_NEXT_STEP_M;
import static util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;

public class AddAdminUserPage extends FluentPage {
    @AjaxElement
    @FindBy(id = "createAdminUserForm")
    protected FluentWebElement createAdminUserForm;

    private String baseUrl;

    @Override
    public String getUrl() {
        return baseUrl + "/#onboarding/add-admin-user";
    }

    public void submitForm(String... inputs) {
        fill("input").with(inputs);
        click("#createAdminUser");
    }

    public void submitForm() {
        fill("input").with();
        click("#createAdminUser");
    }

    public String usernameValidationErrorMessage() {
        return $("#username-error").getText();
    }

    public String passwordValidationErrorMessage() {
        return $("#password-error").getText();
    }

    public boolean isRendered() {
        return createAdminUserForm.isDisplayed();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void waitForSuccessMessage(User user) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until("#actionResultDialog p")
                .hasText(format(ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername()));
    }

    public void waitForFailureMessage(User user) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".isa_error p")
                .hasText(format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername()));
    }

    public String nextActionName() {
        return $("#nextAction").getText();
    }

    public String expectedNextActionName() {
        return ADMIN_USER_ADDITION_NEXT_STEP_M.toUpperCase();
    }

    public void clickNextAction() {
        click("#nextAction");
    }

    public String expectedNextActionUrl() {
        return baseUrl + "/#user/login";
    }

    public void waitForNextPage() {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until("#loginForm").isPresent();
    }
}