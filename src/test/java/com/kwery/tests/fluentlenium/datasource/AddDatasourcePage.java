package com.kwery.tests.fluentlenium.datasource;

import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static com.kwery.models.Datasource.Type.MYSQL;
import static org.openqa.selenium.By.id;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;

public class AddDatasourcePage extends FluentPage implements RepoDashPage {
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

    @Override
    public boolean isRendered() {
        return form.isDisplayed();
    }

    public void waitForSuccessMessage(String label) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, label));
    }

    public void waitForFailureMessage(String label) {
        await().atMost(30, SECONDS).until(".f-failure-message p").hasText(format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, label));
    }

    public void waitForFailureMessage() {
        await().atMost(30, SECONDS).until(".f-failure-message").isDisplayed();
    }

    public List<String> errorMessages() {
        return $(".f-failure-message p").stream().map(FluentWebElement::getText).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public String getUrl() {
        return "/#datasource/add";
    }

    public String actionLabel() {
        return find(id("create")).getText();
    }
}
