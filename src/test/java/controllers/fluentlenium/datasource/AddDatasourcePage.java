package controllers.fluentlenium.datasource;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;

import static controllers.fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static controllers.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static models.Datasource.Type.MYSQL;

public class AddDatasourcePage extends FluentPage {
    @AjaxElement
    @FindBy(id = "addDatasourceForm")
    protected FluentWebElement form;
    protected String baseUrl;

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

    public boolean isRendered() {
        return form.isDisplayed();
    }

    public void waitForSuccessMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_info p").hasText(format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, label));
    }

    public void waitForFailureMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_error p").hasText(format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, label));
    }

    public void waitForFailureMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_error").isDisplayed();
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
        return baseUrl + "/#onboarding/add-datasource";
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
