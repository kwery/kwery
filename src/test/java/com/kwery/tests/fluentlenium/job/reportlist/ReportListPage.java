package com.kwery.tests.fluentlenium.job.reportlist;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M;
import static com.kwery.tests.util.Messages.REPORT_LIST_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

public class ReportListPage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".report-list-f").isDisplayed();
        return true;
    }

    @Override
    public String getUrl() {
        return "/#report/list";
    }

    public void waitForRows(int count) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".report-list-table-body-f tr").hasSize(count);
    }

    public List<ReportListRow> rows() {
        List<ReportListRow> rows = new LinkedList<>();

        for (FluentWebElement tr : $(".report-list-table-body-f tr")) {
            ReportListRow row = new ReportListRow();

            row.setLabel(tr.find(className("label-f")).first().getText());
            row.setReportLink(tr.find(className("label-f")).getAttribute("href"));
            row.setCronExpression(tr.find(className("cron-expression-f")).getText());
            row.setExecutionListLink(tr.find(className("execution-link-f")).getAttribute("href"));
            row.setExecuteNowLink(tr.find(className("execute-now-f")).getAttribute("href"));

            rows.add(row);
        }

        return rows;
    }

    public void deleteReport(int row) {
        click($(".report-list-table-body-f tr").get(row).find(className("delete-f")));
    }

    public void waitForDeleteSuccessMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(REPORT_LIST_DELETE_SUCCESS_M);
    }

    public void waitForDeleteFailureMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M);
    }
}
