package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.tests.fluentlenium.job.save.ReportSavePage;

import static org.openqa.selenium.By.className;

public class ReportUpdatePage extends ReportSavePage {
    protected int reportId;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    @Override
    public String getUrl() {
        return "/#report/" + getReportId();
    }

    public void waitForReportDisplay(String title) {
        await().until($(className("f-report-title"))).hasAttribute("value", title);
    }
}
