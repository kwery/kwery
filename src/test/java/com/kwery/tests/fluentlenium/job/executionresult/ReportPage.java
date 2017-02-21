package com.kwery.tests.fluentlenium.job.executionresult;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;

import java.util.List;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/{jobId}/execution/{executionId}")
public class ReportPage extends KweryFluentPage implements RepoDashPage {
    protected String executionId;
    protected int jobId;
    protected int expectedReportSections;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".report-section-f")).size(getExpectedReportSections());
        return true;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getExpectedReportSections() {
        return expectedReportSections;
    }

    public void setExpectedReportSections(int expectedReportSections) {
        this.expectedReportSections = expectedReportSections;
    }

    public String reportHeader() {
        return $(className("report-title-f")).text();
    }

    public String sectionTitle(int index) {
        return $(className("section-title-f")).get(index).text();
    }

    public boolean isDownloadLinkPresent(int index) {
        return el(className(String.format("download-%d-f", index))).present();
    }

    public boolean isTableDisplayed(int index) {
        return el(className(String.format("table-%d-f", index))).displayed();
    }

    public List<String> tableHeaders(int index) {
        return $(String.format(".table-%d-f thead th", index)).stream().map(FluentWebElement::text).collect(toList());
    }

    public List<String> tableRows(int index, int row) {
        return $(String.format(".table-%d-f tbody tr", index)).get(row).find("td").stream().map(FluentWebElement::text).collect(toList());
    }

    public String downloadReportLink(int index) {
        return el(className(String.format("download-%d-f", index))).attribute("href");
    }

    public boolean isTableEmpty(int index) {
       return !el(String.format("table-%d-f th", index)).present() && !el(String.format("table-%d-f tr", index)).present();
    }

    public String getFailureContent(int index) {
        return $(String.format(".failure-%d-f p", index)).text();
    }

    public String getWarningContent(int index) {
        return $(String.format(".warning-%d-f p", index)).text();
    }
}
