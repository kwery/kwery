package com.kwery.tests.fluentlenium.job;

import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.Map;

import static com.kwery.tests.util.Messages.REPORT_SAVE_SUCCESS_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

public class ReportSavePage extends FluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String SELECT_VALIDATION_ERROR_MESSAGE = "Please select an item in the list.";

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#saveReport").isDisplayed();
        return true;
    }

    public void fillAndSubmitReportSaveForm(JobDto jobDto, Map<Integer, String> datasourceIdToLabelMap) {
        fill(".f-report-title").with(jobDto.getTitle());
        fill(".f-report-label").with(jobDto.getLabel());
        fill(".f-report-cron-expression").with(jobDto.getCronExpression());

        for (int i = 0; i < jobDto.getSqlQueries().size(); ++i) {
            if (i >= 1) {
                clickOnAddSqlQuery(i);
            }

            SqlQueryDto dto = jobDto.getSqlQueries().get(i);
            fill(".f-sql-query" + i + " .f-query").with(dto.getQuery());
            fill(".f-sql-query" + i + " .f-sql-query-label").with(dto.getLabel());
            fillSelect(".f-datasource").withText(datasourceIdToLabelMap.get(dto.getDatasourceId()));
        }

        submitReportSaveForm();
    }

    public void submitReportSaveForm() {
        click(".f-report-submit");
    }

    public void clickOnAddSqlQuery(int i) {
        $(className("f-add-sql-query")).click();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-sql-query" + i).isDisplayed();
    }

    public void clickOnRemoveSqlQuery(int i) {
        $(".f-sql-query" + i + " .f-remove-sql-query").click();
        //TODO - find why this is not working
        //await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-sql-query" + i).isNotDisplayed();
        try {
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
    }

    public void waitForReportSaveSuccessMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(REPORT_SAVE_SUCCESS_MESSAGE_M);
    }

    public int removeSqlQueryActionDisplayedCount() {
        int count = 0;
        for (FluentWebElement fluentWebElement : $(className("f-remove-sql-query"))) {
            if (fluentWebElement.isDisplayed()) {
                count = count + 1;
            }
        }

        return count;
    }

    @Override
    public String getUrl() {
        return "/#report/add";
    }

    public String validationMessage(ReportFormField field) {
        return $(className(format("%s-form-validation-message-f", field.name()))).getText();
    }

    public String validationMessage(SqlQueryFormField field, int index) {
        return $(format(".f-sql-query%d .%s-form-validation-message-f", index, field.name())).getText();
    }

    public void waitForReportFormValidationMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".reportTitle-form-validation-message-f").hasText(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public enum ReportFormField {
        reportTitle, label, cronExpression
    }

    public enum SqlQueryFormField {
        query, datasourceId, queryLabel
    }
}
