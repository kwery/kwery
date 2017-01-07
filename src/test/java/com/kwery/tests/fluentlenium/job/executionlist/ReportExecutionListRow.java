package com.kwery.tests.fluentlenium.job.executionlist;

public class ReportExecutionListRow {
    protected String start;
    protected String end;
    protected String status;
    protected String statusLink;

    public ReportExecutionListRow(String start, String end, String status, String statusLink) {
        this.start = start;
        this.end = end;
        this.status = status;
        this.statusLink = statusLink;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLink() {
        return statusLink;
    }

    public void setStatusLink(String statusLink) {
        this.statusLink = statusLink;
    }
}
