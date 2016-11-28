package com.kwery.tests.fluentlenium.sqlquery;

import com.google.common.collect.ImmutableList;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.tests.util.Messages.QUERY_RUN_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.QUERY_RUN_WITHOUT_CRON_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.Messages.QUERY_RUN_WITH_CRON_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class SqlQueryAddPage extends FluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "queryRunForm")
    protected FluentWebElement form;

    public void submitForm(SqlQueryDto dto, boolean fillCron) {
        fill("textarea").with(dto.getQuery());

        //TODO - Needs to be better
        if (fillCron) {
            fill("input").with(dto.getCronExpression(), dto.getLabel());
        } else {
            fill("input").with(dto.getLabel());
        }

        fillSelect("#datasourceId").withIndex(0);
        if (isDependsOnSqlQueryDisplayed()) {
            fillSelect("#dependsOnSqlQueryId").withIndex(0);
        }
        click("#create");
    }

    public void submitForm() {
        click("#create");
    }

    public void waitForScheduledMessageSuccess() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(QUERY_RUN_WITH_CRON_ADDITION_SUCCESS_M);
    }

    public void waitForDependsOnQueryMessageSuccess() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(QUERY_RUN_WITHOUT_CRON_ADDITION_SUCCESS_M);
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

    public boolean isDependsOnSqlQueryDisplayed() {
       return $(className("f-depends-on-sql-query")).first().isDisplayed();
    }

    public boolean enableDependsOnSqlQueryLinkDisplayed() {
        return $(className("f-enable-depends-on-sql-query")).first().isDisplayed();
    }

    public boolean enableCronExpressionLinkDisplayed() {
        return $(className("f-enable-cron-expression")).first().isDisplayed();
    }

    public boolean isDependsOnSqlQueryEnabled() {
        return $(id("dependsOnSqlQueryId")).first().isEnabled();
    }

    public void awaitTillDependsOnSqlQueryIsEnabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($("#dependsOnSqlQueryId")).isEnabled();
    }

    public boolean isCronExpressionEnabled() {
        return $(id("cronExpression")).first().isEnabled();
    }

    public void clickEnableCronExpression() {
        $(className("f-enable-cron-expression")).click();
    }

    public void clickEnableDependsOnSqlQuery() {
        $(className("f-enable-depends-on-sql-query")).click();
    }

    public void waitForEnableCronExpression() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#cronExpression").isEnabled();
    }

    public void waitForEnableDependsOnSqlQuery() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#dependsOnSqlQueryId").isEnabled();
    }
}
