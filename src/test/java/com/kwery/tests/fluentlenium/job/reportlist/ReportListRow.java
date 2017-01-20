package com.kwery.tests.fluentlenium.job.reportlist;

public class ReportListRow {
    protected String label;
    protected String reportLink;
    protected String cronExpression;
    protected String executionLink;
    protected String executeNowLink;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getReportLink() {
        return reportLink;
    }

    public void setReportLink(String reportLink) {
        this.reportLink = reportLink;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getExecutionLink() {
        return executionLink;
    }

    public void setExecutionListLink(String executionLink) {
        this.executionLink = executionLink;
    }

    public String getExecuteNowLink() {
        return executeNowLink;
    }

    public void setExecuteNowLink(String executeNowLink) {
        this.executeNowLink = executeNowLink;
    }

    @Override
    public String toString() {
        return "ReportListRow{" +
                "label='" + label + '\'' +
                ", reportLink='" + reportLink + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", executionLink='" + executionLink + '\'' +
                ", executeNowLink='" + executeNowLink + '\'' +
                '}';
    }
}
