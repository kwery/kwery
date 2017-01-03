package com.kwery.services.mail;

import com.google.inject.Inject;
import ninja.postoffice.Postoffice;

public class MailService {
    protected final Postoffice postoffice;

    @Inject
    public MailService(Postoffice postoffice) {
        this.postoffice = postoffice;
    }

    public void send(KweryMail kweryMail) throws Exception {
        postoffice.send(kweryMail);
    }

    public Postoffice getPostoffice() {
        return postoffice;
    }
}
