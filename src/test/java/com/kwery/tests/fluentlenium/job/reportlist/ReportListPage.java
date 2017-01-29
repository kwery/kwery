package com.kwery.tests.fluentlenium.job.reportlist;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M;
import static com.kwery.tests.util.Messages.REPORT_LIST_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class ReportListPage extends KweryFluentPage implements RepoDashPage {
    protected int resultCount;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".report-list-f")).displayed();
        return true;
    }

    @Override
    public String getUrl() {
        return String.format("/#report/list/?resultCount=%d", getResultCount());
    }

    public void waitForRows(int count) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".report-list-table-body-f tr")).size(count);
    }

    public List<ReportListRow> rows() {
        List<ReportListRow> rows = new LinkedList<>();

        for (FluentWebElement tr : $(".report-list-table-body-f tr")) {
            ReportListRow row = new ReportListRow();

            row.setLabel(tr.find(className("name-f")).first().text());
            row.setReportLink(tr.find(className("name-f")).attribute("href"));
            row.setCronExpression(tr.find(className("cron-expression-f")).text());
            row.setExecutionListLink(tr.find(className("execution-link-f")).attribute("href"));
            row.setExecuteNowLink(tr.find(className("execute-now-f")).attribute("href"));

            rows.add(row);
        }

        return rows;
    }

    public void deleteReport(int row) {
        ($(".report-list-table-body-f tr").get(row).find(className("delete-f"))).click();
    }

    public void waitForDeleteSuccessMessage() {
        super.waitForSuccessMessage(REPORT_LIST_DELETE_SUCCESS_M);
    }

    public void waitForDeleteFailureMessage() {
        super.waitForFailureMessage(JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M);
    }

    public void selectLabel(int index) {
        $(".label-f").fillSelect().withIndex(index);
    }

    public void filterReport() {
        $(className("report-filter-f")).click();
    }

    public List<String> labelTexts() {
        return $(".label-f option").stream().map(option -> option.text().trim()).collect(toList());
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    //Pagination related - start
    public boolean isNextEnabled() {
        return !Arrays.asList(find(className("next-f")).attribute("class").split(" ")).contains("disabled");
    }

    public boolean isPreviousEnabled() {
        return !Arrays.asList(find(className("previous-f")).attribute("class").split(" ")).contains("disabled");
    }

    public void clickPrevious() {
        $(id("previous")).click();
    }

    public void clickNext() {
        $(id("next")).click();
    }

    public void waitUntilPreviousIsEnabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> !Arrays.asList(find(className("previous-f")).attribute("class").split(" ")).contains("disabled")
        );
    }

    public void waitUntilPreviousIsDisabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> Arrays.asList(find(className("previous-f")).attribute("class").split(" ")).contains("disabled")
        );
    }

    public void waitUntilNextIsDisabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> Arrays.asList(find(className("next-f")).attribute("class").split(" ")).contains("disabled")
        );
    }

    public void waitUntilNextIsEnabled() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(
                () -> !Arrays.asList(find(className("next-f")).attribute("class").split(" ")).contains("disabled")
        );
    }
    //Pagination related - end

    public void waitForFluentField(String value) {
        await().pollingEvery(1, MILLISECONDS).atMost(TIMEOUT_SECONDS, SECONDS).until($(".fluent-field-f")).attribute("value", value);
    }
}
