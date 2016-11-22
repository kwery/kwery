package com.kwery.services.mail;

import com.google.inject.ImplementedBy;
import ninja.postoffice.Mail;

@ImplementedBy(KweryMailImpl.class)
public interface KweryMail extends Mail {
}
