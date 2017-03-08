package com.kwery.tests.fluentlenium.job.save.update;

import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
public class ReportUpdatePage extends ReportSavePage {
    protected boolean isCopy;

    public void waitForReportDisplay(String name) {
        await().until($(className("f-report-name"))).attribute("value", name);
    }

    @Override
    public String getUrl() {
        if (isCopy) {
            return "/#report/{reportId}/copy";
        } else {
            return "/#report/{reportId}";
        }
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
    }
}
