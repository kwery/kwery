package com.kwery.tests.fluentlenium.job.save.update;

import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/{reportId}")
public class ReportUpdatePage extends ReportSavePage {
    protected int reportId;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void waitForReportDisplay(String name) {
        await().until($(className("f-report-name"))).attribute("value", name);
    }
}
