package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/add")
public class UserAddPage extends KweryFluentPage implements RepoDashPage {
    @Wait(timeout = TIMEOUT_SECONDS, timeUnit = SECONDS)
    @FindBy(className = "add-user-form-f")
    protected FluentWebElement createAdminUserForm;

    public void submitForm(String... inputs) {
        $("input").fill().with(inputs);
        $(".user-save-f").click();
    }

    public void submitForm() {
        $(".user-save-f").click();
    }

    public void assertNonEmptyUsernameValidationErrorMessage() {
        assertThat(el("div", withClass().contains("username-error-f"), withTextContent().notContains("")));
    }

    public void assertNotEmptyPasswordValidationErrorMessage() {
        assertThat(el("div", withClass().contains("password-error-f"), withTextContent().notContains("")));
    }

    @Override
    public boolean isRendered() {
        return createAdminUserForm.displayed();
    }

    public void waitForSuccessMessage(User user) {
        waitForSuccessMessage(format(ADMIN_USER_ADDITION_SUCCESS_M, user.getUsername()));
    }

    public void waitForFailureMessage(User user) {
        waitForFailureMessage(format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername()));
    }

    public void waitForUserListPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(getBaseUrl() + "/#user/list"));
    }
}
