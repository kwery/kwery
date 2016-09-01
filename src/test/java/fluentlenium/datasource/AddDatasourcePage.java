package fluentlenium.datasource;

import fluentlenium.RepoDashFluentLeniumTest;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static models.Datasource.Type.MYSQL;
import static util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static util.Messages.DATASOURCE_ADDITION_SUCCESS_M;

public class AddDatasourcePage extends FluentPage {
    @AjaxElement
    @FindBy(id = "addDatasourceForm")
    protected FluentWebElement form;

    public void submitForm(String... params) {
        fill("input").with(params);
        click("#create");
    }

    public void submitForm() {
        fill("input").with();
        click("#create");
    }

    public String usernameValidationErrorMessage() {
        return $("#username-error").getText();
    }

    public String urlValidationErrorMessage() {
        return $("#url-error").getText();
    }

    public String labelValidationErrorMessage() {
        return $("#label-error").getText();
    }

    public String portValidationErrorMessage() {
        return $("#port-error").getText();
    }

    public boolean isRendered() {
        return form.isDisplayed();
    }

    public void waitForSuccessMessage(String label) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".isa_info p").hasText(format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, label));
    }

    public void waitForFailureMessage(String label) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".isa_error p").hasText(format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, label));
    }

    public void waitForFailureMessage() {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".isa_error").isDisplayed();
    }

    public List<String> errorMessages() {
        List<String> messages = new LinkedList<>();
        for (FluentWebElement e : $(".isa_error p")) {
            messages.add(e.getText());
        }
        return messages;
    }

    @Override
    public String getUrl() {
        return "/#onboarding/add-datasource";
    }
}
