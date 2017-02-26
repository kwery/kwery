package com.kwery.tests.fluentlenium.job.reportlist;

import com.google.common.base.Strings;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;
import org.openqa.selenium.support.FindBy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.kwery.controllers.apis.JobApiController.DISPLAY_DATE_FORMAT;
import static com.kwery.tests.fluentlenium.job.reportlist.ReportListPage.PaginationPosition.top;
import static com.kwery.tests.fluentlenium.job.reportlist.ReportListPage.ReportList.*;
import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.openqa.selenium.By.tagName;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/list/?resultCount={resultCount}")
public class ReportListPage extends KweryFluentPage implements RepoDashPage {
    protected int resultCount;

    @FindBy(css = ".pagination-top-f")
    protected PaginationComponent topPaginationComponent;

    @FindBy(css = ".pagination-bottom-f")
    protected PaginationComponent bottomPaginationComponent;

    protected ActionResultComponent actionResultComponent;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".report-list-f")).displayed();
        return true;
    }

    public void assertReportList(int count) {
        assertThat($(".report-list-f").withHook(WaitHook.class).index(count)).isDisplayed();
    }

    public void assertReportListRow(int row, Map<ReportList, ?> values) throws ParseException {
        assertThat(el(String.format(".report-list-%d-f .title-f", row), withText(String.valueOf(values.get(title))))).isDisplayed();
        assertThat(el(String.format(".report-list-%d-f .name-f", row), withText(String.valueOf(values.get(name))))).isDisplayed();
        assertThat(el(String.format(".report-list-%d-f .last-execution-f", row), withText(String.valueOf(values.get(lastExecution))))).isDisplayed();

        if (!"".equals(Strings.nullToEmpty(String.valueOf(values.get(nextExecution))))) {
            assertThat(el(String.format(".report-list-%d-f .next-execution-f", row), withTextContent().notContains(""))).isDisplayed();
            new SimpleDateFormat(DISPLAY_DATE_FORMAT).parse(el(String.format(".report-list-%d-f .next-execution-f", row)).text().trim());
        } else {
            assertThat(el(String.format(".report-list-%d-f .next-execution-f", row), withText().equalTo(""))).isDisplayed();
        }

        List<String> labels = (List<String>) values.get(ReportList.labels);

        for (String label : labels) {
            assertThat($(String.format(".report-list-%d-f .label-f", row), containingText(label))).hasSize(1);
        }

        assertThat(el(String.format(".report-list-%d-f .edit-f", row), with("href").contains(String.valueOf(values.get(reportEditLink))))).isDisplayed();
        assertThat(el(String.format(".report-list-%d-f .execution-link-f", row), with("href").contains(String.valueOf(values.get(executionsLink))))).isDisplayed();
    }

    public void deleteReport(int row) {
        $(tagName("div"), withClass().contains(String.format("report-list-%d-f", row))).$(".delete-f").click();
    }

    public void assertDeleteSuccessMessage() {
        actionResultComponent.assertSuccessMessage(REPORT_LIST_DELETE_SUCCESS_M);
    }

    public void assertDeleteFailureMessage() {
        actionResultComponent.assertFailureMessage(JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M);
    }

    public void selectLabel(int index) {
        $("select", withClass().contains("label-f")).fillSelect().withIndex(index);
    }

    public void assertLabelTexts(Collection<String> labels) {
        for (String label : labels) {
            assertThat(el(".label-f option", withText().contains(label))).isDisplayed();
        }
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public void assertStartingPage() {
        assertThat(getDriver().getCurrentUrl(), containsString("pageNumber=0"));
    }

    public PaginationComponent getTopPaginationComponent() {
        return topPaginationComponent;
    }

    public enum ReportList {
        title, name, lastExecution, nextExecution, labels, executionsLink, reportEditLink
    }

    public void search(String search) {
        el("input", withClass().contains("search-f")).fill().with(search);
        el(".search-submit-f").withHook(WaitHook.class).click();
    }

    public void assertInvalidSearchCharacters() {
        actionResultComponent.assertFailureMessage(REPORT_LIST_SEARCH_INVALID_M);
    }

    public PaginationComponent getBottomPaginationComponent() {
        return bottomPaginationComponent;
    }

    public PaginationComponent getPaginationComponent(PaginationPosition position) {
        if (position == top) {
            return getTopPaginationComponent();
        } else {
            return getBottomPaginationComponent();
        }
    }

    public enum PaginationPosition {
        top, bottom
    }
}
