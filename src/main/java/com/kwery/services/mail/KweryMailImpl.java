package com.kwery.services.mail;

import ninja.postoffice.common.MailImpl;

import java.util.LinkedList;
import java.util.List;

public class KweryMailImpl extends MailImpl implements KweryMail {
    protected List<KweryMailAttachment> attachments = new LinkedList<>();

    @Override
    public List<KweryMailAttachment> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(List<KweryMailAttachment> attachments) {
        this.attachments = attachments;
    }
}
