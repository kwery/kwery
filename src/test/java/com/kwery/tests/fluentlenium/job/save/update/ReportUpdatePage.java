package com.kwery.tests.fluentlenium.job.save.update;

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

    public void waitForReportDisplay(String name) {
        await().until($(className("f-report-name"))).attribute("value", name);
    }
}
