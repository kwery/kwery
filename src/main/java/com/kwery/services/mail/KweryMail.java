package com.kwery.services.mail;

import com.google.inject.ImplementedBy;
import ninja.postoffice.Mail;

import java.util.List;

@ImplementedBy(KweryMailImpl.class)
public interface KweryMail extends Mail {
    public List<KweryMailAttachment> getAttachments();
    public void setAttachments(List<KweryMailAttachment> attachments);
}
