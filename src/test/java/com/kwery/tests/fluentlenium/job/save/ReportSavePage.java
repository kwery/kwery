package com.kwery.tests.fluentlenium.job.save;

import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.SqlQueryEmailSettingModel;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
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

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/add")
public class ReportSavePage extends KweryFluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String SELECT_VALIDATION_ERROR_MESSAGE = "Please select an item in the list.";

    protected Map<Integer, String> datasourceIdToLabelMap;
    protected Map<Integer, String> parentJobIdToLabelMap;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($("#saveReport")).displayed();
        return true;
    }

    public void fillAndSubmitReportSaveForm(JobForm jobForm) {
        fillReportSaveForm(jobForm);
        submitReportSaveForm();
    }

    public void fillReportSaveForm(JobForm jobForm) {
        $(".f-report-title").fill().with(jobForm.getTitle());
        $(".f-report-name").fill().with(jobForm.getName());

        //TODO - Tests with empty emails etc
        if (isEmailFieldEnabled()) {
            $(".f-report-emails").fill().with(String.join(",", jobForm.getEmails()));
        }

        if (isFailureAlertEmailFieldEnabled()) {
            $(".failure-alert-emails-f").fill().with(String.join(",", jobForm.getFailureAlertEmails()));
        }

        if ($(className("parent-report-option-f")).first().selected()) {
            $(".f-parent-report").fillSelect().withText(parentJobIdToLabelMap.get(jobForm.getParentJobId()));
        } else if (jobForm.isUseCronUi()) {
            chooseCronUi();
        } else {
            chooseCronExpression();
            $(".f-report-cron-expression").fill().with(jobForm.getCronExpression());
        }

        for (int i = 0; i < jobForm.getSqlQueries().size(); ++i) {
            if (i >= 1) {
                clickOnAddSqlQuery(i);
            }

            SqlQueryDto dto = jobForm.getSqlQueries().get(i);
            $(".f-sql-query" + i + " .f-query").fill().with(dto.getQuery());
            $(".f-sql-query" + i + " .f-sql-query-label").fill().with(dto.getLabel());
            $(".f-sql-query" + i + " .f-sql-query-title").fill().with(dto.getTitle());
            $(".f-sql-query" + i + " .f-datasource").fillSelect().withText(datasourceIdToLabelMap.get(dto.getDatasourceId()));

            if (dto.getSqlQueryEmailSetting() != null) {
                SqlQueryEmailSettingModel model = dto.getSqlQueryEmailSetting();
                String aCls = String.format(".f-sql-query%d .include-attachment-f", i);
                if (model.getIncludeInEmailAttachment()) {
                    if (!el(aCls).selected()) {
                        el(aCls).click();
                    }
                } else {
                    if (el(aCls).selected()) {
                        el(aCls).click();
                    }
                }

                String iCls = String.format(".f-sql-query%d .include-body-f", i);
                if (model.getIncludeInEmailBody()) {
                    if (!el(iCls).selected()) {
                        el(iCls).click();
                    }
                } else {
                    if (el(iCls).selected()) {
                        el(iCls).click();
                    }
                }
            }
        }

        if (isEmptyReportNoEmailRuleEnabled()) {
            if (jobForm.isEmptyReportNoEmailRule()) {
                ensureEmailRuleChecked();
            } else {
                ensureEmailRuleUnchecked();
            }
        }

        int i = 0;
        for (Integer id : jobForm.getLabelIds()) {
            clickOnAddLabel(i);
            selectLabel(id, i);
            i = i + 1;
        }

        if (jobForm.getJobRuleModel() != null) {
            setSequentialSqlQueryExecution(jobForm.getJobRuleModel().isSequentialSqlQueryExecution());
            if (jobForm.getJobRuleModel().isSequentialSqlQueryExecution()) {
                awaitUntilIsStopExecutionOnSqlQueryDisplayed();
                setStopExecutionOnSqlQueryFailure(jobForm.getJobRuleModel().isStopExecutionOnSqlQueryFailure());
            }
        }
    }

    public void ensureEmailRuleUnchecked() {
        if (el(className("no-email-rule-f")).selected()) {
            el(className("no-email-rule-f")).click();
        }
    }

    public void ensureEmailRuleChecked() {
        if (!el(className("no-email-rule-f")).selected()) {
            await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".no-email-rule-f")).clickable();
            el(className("no-email-rule-f")).click();
        }
    }

    public void selectLabel(Integer labelId, int index) {
        $(String.format(".select-%d-f", index)).fillSelect().withValue(String.valueOf(labelId));
    }

    public void submitReportSaveForm() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-report-submit")).clickable();
        $(".f-report-submit").click();
    }

    public void clickOnAddSqlQuery(int i) {
        $(className("f-add-sql-query")).click();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-sql-query" + i)).displayed();
    }

    public void clickOnAddLabel(int i) {
        $(className("add-label-f")).click();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(String.format(".label-%d-f", i))).displayed();
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
        if (count > 1) {
            await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".label-f")).size(count - 1);
        } else {
            waitForElementDisappearance(className(".label-f"));
        }
    }

    public void waitForReportSaveSuccessMessage() {
        super.waitForSuccessMessage(REPORT_SAVE_SUCCESS_MESSAGE_M);
    }

    public List<String> selectedLabels() {
        List<String> labels = new LinkedList<>();

        for (FluentWebElement select : $(className("label-f"))) {
            for (FluentWebElement option : select.find(By.tagName("option"))) {
                if (option.selected()) {
                    labels.add(option.text());
                }
            }
        }

        return labels;
    }

    public int removeSqlQueryActionDisplayedCount() {
        int count = 0;
        for (FluentWebElement fluentWebElement : $(className("f-remove-sql-query"))) {
            if (fluentWebElement.displayed()) {
                count = count + 1;
            }
        }

        return count;
    }

    public String validationMessage(ReportFormField field) {
        return $(className(format("%s-form-validation-message-f", field.name()))).text();
    }

    public String validationMessage(SqlQueryFormField field, int index) {
        return $(format(".f-sql-query%d .%s-form-validation-message-f", index, field.name())).text();
    }

    public boolean sqlQueryEmailSettingIncludeInEmailBody(int index) {
        return el(format(".f-sql-query%d .include-body-f", index)).selected();
    }

    public boolean sqlQueryEmailSettingIncludeAsEmailAttachment(int index) {
        return el(format(".f-sql-query%d .include-attachment-f", index)).selected();
    }

    public void waitForReportFormValidationMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".reportTitle-form-validation-message-f")).text(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public enum ReportFormField {
        reportTitle, name, cronExpression, parentReportId
    }

    public enum SqlQueryFormField {
        query, datasourceId, queryTitle, queryLabel
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

    public List<String> labelTexts(int index) {
        return $(String.format(".select-%d-f option", index)).stream().map(option -> option.text().trim()).collect(toList());
    }

    public int labelSelectCount() {
        return $(className("label-f")).size();
    }

    public void waitForReportListPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(getBaseUrl() + "/#report/list"));
    }

    public boolean isEmailFieldEnabled() {
        return el(className("f-report-emails")).enabled();
    }

    public boolean isFailureAlertEmailFieldEnabled() {
        return el(className("failure-alert-emails-f")).enabled();
    }

    public boolean isEmptyReportNoEmailRuleEnabled() {
        return el(className("no-email-rule-f")).enabled();
    }

    public boolean isSequentialSqlQueryExecution() {
        return el(className("sequential-sql-query-execution-f")).selected();
    }

    public boolean isStopExecutionOnSqlQueryFailure() {
        return el(className("stop-execution-on-sql-query-failure-f")).selected();
    }

    public boolean isStopExecutionOnSqlQueryDisplayed() {
        return el(className("stop-execution-on-sql-query-failure-f")).displayed();
    }

    public void awaitUntilIsStopExecutionOnSqlQueryDisplayed() {
        await().atMost(TestUtil.TIMEOUT_SECONDS, SECONDS).until(el(className("stop-execution-on-sql-query-failure-f"))).displayed();
    }

    public void awaitUntilIsStopExecutionOnSqlQueryNotDisplayed() {
        await().atMost(TestUtil.TIMEOUT_SECONDS, SECONDS).until(el(className("stop-execution-on-sql-query-failure-f"))).not().displayed();
    }

    public void setSequentialSqlQueryExecution(boolean checked) {
        if (checked) {
            if (!isSequentialSqlQueryExecution()) {
                el(className("sequential-sql-query-execution-f")).click();
            }
        } else {
            if (isSequentialSqlQueryExecution()) {
                el(className("sequential-sql-query-execution-f")).click();
            }
        }
    }

    public void setStopExecutionOnSqlQueryFailure(boolean checked) {
        if (checked) {
            if (!isStopExecutionOnSqlQueryFailure()) {
                el(className("stop-execution-on-sql-query-failure-f")).click();
            }
        } else {
            if (isStopExecutionOnSqlQueryFailure()) {
                el(className("stop-execution-on-sql-query-failure-f")).click();
            }
        }
    }
}
