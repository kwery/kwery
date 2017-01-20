package com.kwery.tests.fluentlenium.datasource;

import com.google.common.base.Supplier;
import com.kwery.models.Datasource;
import com.kwery.models.Datasource.Type;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.models.Datasource.Type.POSTGRESQL;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class DatasourceAddPage extends KweryFluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String SELECT_VALIDATION_ERROR_MESSAGE = "Please select an item in the list.";

    @AjaxElement
    @FindBy(id = "addDatasourceForm")
    protected FluentWebElement form;

    public void submitForm(Datasource datasource) {
        fillSelect(".type-f").withText(datasource.getType().name());

        if (datasource.getType() == POSTGRESQL) {
            waitForDatabaseFormFieldToBeVisible();
            fill(".database-f").with(datasource.getDatabase());
        }

        fill(".url-f").with(datasource.getUrl());
        fill(".port-f").with(String.valueOf(datasource.getPort()));
        fill(".username-f").with(datasource.getUsername());
        fill(".password-f").with(datasource.getPassword());
        fill(".label-f").with(datasource.getLabel());

        click("#create");
    }

    public void submitForm() {
        click(".save-datasource-f");
    }

    @Override
    public boolean isRendered() {
        return form.isDisplayed();
    }

    public void waitForSuccessMessage(String label, Type type) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(format(DATASOURCE_ADDITION_SUCCESS_M, type.name(), label));
    }

    public void waitForFailureMessage(String label, Type type) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(format(DATASOURCE_ADDITION_FAILURE_M, type.name(), label));
    }

    public void waitForFailureMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message").isDisplayed();
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

    public String validationMessage(FormField formField) {
        By locator = className(String.format("%s-error-f", formField.name()));
        return $(locator).getText();
    }

    public void waitForReportFormValidationMessage(FormField formField, String message) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(String.format(".%s-error-f", formField)).hasText(message);
    }

    public void waitForDatabaseFormFieldToBeVisible() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".database-f").isDisplayed();
    }

    public void waitForDatabaseFormFieldToBeInvisible() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".database-f").isNotDisplayed();
    }

    public boolean isDatabaseFormFieldVisible() {
        return $(className("database-f")).first().isDisplayed();
    }

    public void selectDatasourceType(Type type) {
        fillSelect(".type-f").withText(type.name());
    }

    public enum FormField {
        type, url, database, port, username, password, label
    }

    public void waitForDatasourceListPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return url().equals("/#datasource/list");
            }
        });
    }
}
