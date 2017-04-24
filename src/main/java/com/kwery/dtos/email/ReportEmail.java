package com.kwery.dtos.email;

import java.util.LinkedList;
import java.util.List;

public class ReportEmail {
    protected List<ReportEmailSection> reportEmailSections = new LinkedList<>();
    protected boolean attachmentSkipped;
    protected String logoUrl;

    public List<ReportEmailSection> getReportEmailSections() {
        return reportEmailSections;
    }

    public void setReportEmailSections(List<ReportEmailSection> reportEmailSections) {
        this.reportEmailSections = reportEmailSections;
    }

    public boolean isAttachmentSkipped() {
        return attachmentSkipped;
    }

    public void setAttachmentSkipped(boolean attachmentSkipped) {
        this.attachmentSkipped = attachmentSkipped;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
