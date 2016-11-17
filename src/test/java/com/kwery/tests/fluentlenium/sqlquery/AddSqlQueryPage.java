package com.kwery.tests.fluentlenium.sqlquery;

import com.google.common.collect.ImmutableList;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static com.kwery.tests.util.Messages.QUERY_RUN_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.QUERY_RUN_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class AddSqlQueryPage extends FluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "queryRunForm")
    protected FluentWebElement form;

    public void submitForm(SqlQueryDto dto) {
        fill("textarea").with(dto.getQuery());
        fill("input").with(dto.getCronExpression(), dto.getLabel());
        fillSelect("#datasourceId").withIndex(0);
        click("#create");
    }

    public void submitForm() {
        click("#create");
    }

    public void waitForSuccessMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(QUERY_RUN_ADDITION_SUCCESS_M);
    }

    public void waitForDuplicateLabelMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(format(QUERY_RUN_ADDITION_FAILURE_M, label));
    }

    public List<String> validationMessages() {
        return ImmutableList.of(
                $("#query-error").getText(),
                $("#label-error").getText()
        );
    }

    @Override
    public boolean isRendered() {
        return form.isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/add";
    }
}
