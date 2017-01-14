package com.kwery.tests.fluentlenium.job.save;

import com.kwery.dtos.SqlQueryDto;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.util.Messages.REPORT_SAVE_SUCCESS_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;

public class ReportSavePage extends FluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String SELECT_VALIDATION_ERROR_MESSAGE = "Please select an item in the list.";

    protected Map<Integer, String> datasourceIdToLabelMap;
    protected Map<Integer, String> parentJobIdToLabelMap;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until("#saveReport").isDisplayed();
        return true;
    }

    public void fillAndSubmitReportSaveForm(JobForm jobForm) {
        fill(".f-report-title").with(jobForm.getTitle());
        fill(".f-report-name").with(jobForm.getName());

        //TODO - Tests with empty emails etc
        fill(".f-report-emails").with(String.join(",", jobForm.getEmails()));

        if ($(className("parent-report-option-f")).first().isSelected()) {
            fillSelect(".f-parent-report").withText(parentJobIdToLabelMap.get(jobForm.getParentJobId()));
        } else if (jobForm.isUseCronUi()) {
            chooseCronUi();
        } else {
            chooseCronExpression();
            fill(".f-report-cron-expression").with(jobForm.getCronExpression());
        }

        for (int i = 0; i < jobForm.getSqlQueries().size(); ++i) {
            if (i >= 1) {
                clickOnAddSqlQuery(i);
            }

            SqlQueryDto dto = jobForm.getSqlQueries().get(i);
            fill(".f-sql-query" + i + " .f-query").with(dto.getQuery());
            fill(".f-sql-query" + i + " .f-sql-query-label").with(dto.getLabel());
            fill(".f-sql-query" + i + " .f-sql-query-title").with(dto.getTitle());
            fillSelect(".f-sql-query" + i + " .f-datasource").withText(datasourceIdToLabelMap.get(dto.getDatasourceId()));
        }

        int i = 0;
        for (Integer id : jobForm.getLabelIds()) {
            clickOnAddLabel(i);
            selectLabel(id, i);
            i = i + 1;
        }

        submitReportSaveForm();
    }

    public void selectLabel(Integer labelId, int index) {
        fillSelect(String.format(".select-%d-f", index)).withValue(String.valueOf(labelId));
    }

    public void submitReportSaveForm() {
        click(".f-report-submit");
    }

    public void clickOnAddSqlQuery(int i) {
        $(className("f-add-sql-query")).click();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-sql-query" + i).isDisplayed();
    }

    public void clickOnAddLabel(int i) {
        $(className("add-label-f")).click();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(String.format(".label-%d-f", i)).isDisplayed();
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

    public void clickOnRemoveLabel(int i) {
        int count = $(className("label-f")).size();
        $(className(String.format("remove-label-%d-f", i))).click();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".label-f").hasSize(count - 1);
    }

    public void waitForReportSaveSuccessMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(REPORT_SAVE_SUCCESS_MESSAGE_M);
    }

    public List<String> selectedLabels() {
        List<String> labels = new LinkedList<>();

        for (FluentWebElement select : $(className("label-f"))) {
            for (FluentWebElement option : select.find(By.tagName("option"))) {
                if (option.isSelected()) {
                    labels.add(option.getText());
                }
            }
        }

        return labels;
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
        reportTitle, name, cronExpression, parentReportId
    }

    public enum SqlQueryFormField {
        query, datasourceId, queryTitle, queryLabel
    }

    public void waitForErrorMessages() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").isDisplayed();
    }

    public List<String> getErrorMessages() {
        return $(".f-failure-message p").stream().map(FluentWebElement::getText).collect(toList());
    }

    public void setDatasourceIdToLabelMap(Map<Integer, String> datasourceIdToLabelMap) {
        this.datasourceIdToLabelMap = datasourceIdToLabelMap;
    }

    public void setParentJobIdToLabelMap(Map<Integer, String> parentJobIdToLabelMap) {
        this.parentJobIdToLabelMap = parentJobIdToLabelMap;
    }

    public void chooseParentReport() {
        $(className("parent-report-option-f")).click();
    }

    public void chooseCronUi() {
        $(className("cron-ui-option-f")).click();
    }

    public void chooseCronExpression() {
        $(className("cron-expression-option-f")).click();
    }

    public String labelText(int index) {
        return $(className(String.format("parent-label-%d-f", index))).getText().trim();
    }

    public List<String> labelTexts(int index) {
        return $(String.format(".select-%d-f option", index)).stream().map(option -> option.getText().trim()).collect(toList());
    }

    public int labelSelectCount() {
        return $(className("label-f")).size();
    }
}
