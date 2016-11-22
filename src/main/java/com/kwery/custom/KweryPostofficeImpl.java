package com.kwery.custom;

import com.google.inject.Inject;
import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.SmtpService;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.commonsmail.CommonsmailHelper;
import org.apache.commons.mail.MultiPartEmail;

public class KweryPostofficeImpl implements Postoffice {
    private final CommonsmailHelper commonsmailHelper;
    private final SmtpService smtpService;

    @Inject
    public KweryPostofficeImpl(CommonsmailHelper commonsmailHelper, SmtpService smtpService) {
        this.commonsmailHelper = commonsmailHelper;
        this.smtpService = smtpService;
    }

    @Override
    public void send(Mail mail) throws Exception {
        // create a correct multipart email based on html / txt content:
        MultiPartEmail multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        // fill the from, to, bcc, css and all other fields:
        commonsmailHelper.doPopulateMultipartMailWithContent(multiPartEmail, mail);

        // set server parameters so we can send the MultiPartEmail:
        SmtpConfiguration smtpConfiguration = smtpService.getSmtpConfiguration();

        String smtpHost = smtpConfiguration.getHost();
        int smtpPort = smtpConfiguration.getPort();
        boolean smtpSsl = smtpConfiguration.isSsl();
        String smtpUser = smtpConfiguration.getUsername();
        String smtpPassword = smtpConfiguration.getPassword();
        boolean smtpDebug = true;

        commonsmailHelper.doSetServerParameter(multiPartEmail, smtpHost, smtpPort, smtpSsl,
                smtpUser, smtpPassword, smtpDebug);

        // And send it:
        multiPartEmail.send();
    }
}
