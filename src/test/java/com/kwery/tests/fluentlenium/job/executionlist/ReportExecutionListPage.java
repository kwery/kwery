package com.kwery.tests.fluentlenium.job.executionlist;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import com.kwery.tests.fluentlenium.job.reportlist.PaginationComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.util.Map;

import static com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.PaginationPosition.top;
import static com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.ReportExecution.*;
import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/{jobId}/execution-list/?resultCount={resultCount}")
public class ReportExecutionListPage extends KweryFluentPage implements RepoDashPage {
    protected int jobId;
    protected int resultCount;
    protected ActionResultComponent actionResultComponent;

    @FindBy(css = ".pagination-top-f")
    protected PaginationComponent topPaginationComponent;

    @FindBy(css = ".pagination-bottom-f")
    protected PaginationComponent bottomPaginationComponent;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".execution-list-container-f")).displayed();
        return true;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public void filterResult(String startDate, String endDate) {
        fillStart(startDate);
        fillEnd(endDate);
        clickFilter();
    }

    public void clickFilter() {
        $(".filter-submit-f").click();
    }

    public void fillEnd(String endDate) {
        $(".filter-end-f").withHook(WaitHook.class).fill().with(endDate);
    }

    public void fillStart(String startDate) {
        $(".filter-start-f").withHook(WaitHook.class).fill().with(startDate);
    }

    public void waitForStartValidationError() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".start-error-f")).text(REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_START_M);
    }

    public void waitForStartValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".start-error-f")).text("");
    }

    public void waitForEndValidationError() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".end-error-f")).text(REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_END_M);
    }

    public void waitForEndValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".end-error-f")).text("");
    }

    public void removeCalendarDropDown() {
        //https://bugs.chromium.org/p/chromedriver/issues/detail?id=35
        Actions actions = new Actions(getDriver());
        actions.moveToElement(getDriver().findElement(className("execution-list-container-f")));
        actions.click();
        actions.sendKeys(Keys.ESCAPE);
        actions.build().perform();
    }

    public void deleteExecution(int row) {
        el("div", withClass().contains(String.format("report-execution-list-%d-f", row))).el("button.delete-f").withHook(WaitHook.class).click();
    }

    public void assertDeleteSuccessMessage() {
        actionResultComponent.assertSuccessMessage(REPORT_JOB_EXECUTION_DELETE_M);
    }

    public enum ReportExecution {
        start, end, status, reportLink
    }

    public void assertRows(int count) {
        assertThat(el(".execution-list-container-f",
                withPredicate(fluentWebElement -> fluentWebElement.$(".report-execution-list-row-f").size() == count))).isDisplayed();
    }

    public void assertReportExecutionList(int row, Map<ReportExecution, ?> map) {
        assertThat(el(String.format(".report-execution-list-%d-f .start-f", row), withText(String.valueOf(map.get(start))))).isDisplayed();
        assertThat(el(String.format(".report-execution-list-%d-f .end-f", row), withText(String.valueOf(map.get(end))))).isDisplayed();
        assertThat(el(String.format(".report-execution-list-%d-f .status-f", row), withText(String.valueOf(map.get(status))))).isDisplayed();

        if (map.containsKey(reportLink)) {
            assertThat(el(String.format(".report-execution-list-%d-f .report-link-f", row), with("href").contains(String.valueOf(map.get(reportLink)))))
                    .isDisplayed();
        } else {
            assertThat(el(String.format(".report-execution-list-%d-f", row),
                    withPredicate(fluentWebElement -> fluentWebElement.$(".actions-f").size() == 1))).isDisplayed();
            assertThat(el(String.format(".report-execution-list-%d-f button", row), withClass().contains("delete-f"))).isDisplayed();
        }
    }

    public PaginationComponent getPaginationComponent(PaginationPosition position) {
        if (position == top) {
            return topPaginationComponent;
        } else {
            return bottomPaginationComponent;
        }
    }

    public enum PaginationPosition {
        top, bottom
    }
}
