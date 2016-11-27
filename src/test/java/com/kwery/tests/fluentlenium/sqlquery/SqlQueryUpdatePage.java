package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.id;
import static com.kwery.tests.util.Messages.QUERY_RUN_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.QUERY_RUN_UPDATE_SUCCESS_M;

public class SqlQueryUpdatePage extends FluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "queryRunForm")
    protected FluentWebElement form;

    @Override
    public boolean isRendered() {
        return form.isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/1";
    }

    public void fillLabel(String label) {
        fill("#label").with(label);
    }

    public void fillCronExpression(String cronExpression) {
        fill("#cronExpression").with(cronExpression);
    }

    public void fillQuery(String query) {
        fill("#query").with(query);
    }

    public void selectDatasource(int dropDownIndex) {
        fillSelect("#datasourceId").withIndex(dropDownIndex);
    }

    public void submit() {
        find(id("create")).submit();
    }

    public void waitForSuccessMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(QUERY_RUN_UPDATE_SUCCESS_M);
    }

    public void waitForDuplicateLabelMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(format(QUERY_RUN_ADDITION_FAILURE_M, label));
    }

    public void waitForForm(String fieldName, String value) {
        await().atMost(30, SECONDS).until(By.name(fieldName)).with("value").startsWith(value);
    }

    public String actionLabel() {
        return find(id("create")).getText();
    }
}
