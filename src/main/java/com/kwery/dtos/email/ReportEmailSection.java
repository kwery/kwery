package com.kwery.dtos.email;

import java.util.LinkedList;
import java.util.List;

public class ReportEmailSection {
    protected String title;
    protected List<String> headers = new LinkedList<>();
    protected List<List<String>> rows = new LinkedList<>();
    protected boolean contentTooLarge;
    protected boolean attachmentTooLarge;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public boolean isContentTooLarge() {
        return contentTooLarge;
    }

    public void setContentTooLarge(boolean contentTooLarge) {
        this.contentTooLarge = contentTooLarge;
    }

    public boolean isAttachmentTooLarge() {
        return attachmentTooLarge;
    }

    public void setAttachmentTooLarge(boolean attachmentTooLarge) {
        this.attachmentTooLarge = attachmentTooLarge;
    }
}
