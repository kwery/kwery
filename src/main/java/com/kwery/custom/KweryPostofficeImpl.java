package com.kwery.custom;

import com.google.inject.Inject;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.EmailConfigurationService;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.KweryMailAttachment;
import com.kwery.services.mail.smtp.SmtpService;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.commonsmail.CommonsmailHelper;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KweryPostofficeImpl implements Postoffice {
    private final CommonsmailHelper commonsmailHelper;
    private final SmtpService smtpService;
    private final EmailConfigurationService emailConfigurationService;

    @Inject
    public KweryPostofficeImpl(CommonsmailHelper commonsmailHelper, SmtpService smtpService, EmailConfigurationService emailConfigurationService) {
        this.commonsmailHelper = commonsmailHelper;
        this.smtpService = smtpService;
        this.emailConfigurationService = emailConfigurationService;
    }

    @Override
    public void send(Mail mail) throws Exception {
        EmailConfiguration emailConfiguration = emailConfigurationService.getEmailConfiguration();
        mail.setFrom(emailConfiguration.getFrom());

        if (emailConfiguration.getBcc() != null) {
            mail.getBccs().clear();
            mail.addBcc(emailConfiguration.getBcc());
        }

        if (emailConfiguration.getReplyTo() != null) {
            mail.getReplyTo().clear();;
            mail.addReplyTo(emailConfiguration.getReplyTo());
        }

        // create a correct multipart email based on html / txt content:
        MultiPartEmail multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        // fill the from, to, bcc, css and all other fields:
        commonsmailHelper.doPopulateMultipartMailWithContent(multiPartEmail, mail);

        attachAttachments(multiPartEmail, (KweryMail) mail);

        // set server parameters so we can send the MultiPartEmail:
        SmtpConfiguration smtpConfiguration = smtpService.getSmtpConfiguration();

        String smtpHost = smtpConfiguration.getHost();
        int smtpPort = smtpConfiguration.getPort();
        boolean smtpSsl = smtpConfiguration.isSsl();
        String smtpUser = smtpConfiguration.getUsername();
        String smtpPassword = smtpConfiguration.getPassword();
        boolean smtpDebug = false;

        if (smtpConfiguration.isUseLocalSetting()) {
            commonsmailHelper.doSetServerParameter(multiPartEmail, smtpHost, smtpPort, false, "", "", smtpDebug);
        } else {
            commonsmailHelper.doSetServerParameter(multiPartEmail, smtpHost, smtpPort, smtpSsl, smtpUser, smtpPassword, smtpDebug);
        }

        // And send it:
        multiPartEmail.send();
    }

    protected void attachAttachments(MultiPartEmail multiPartEmail, KweryMail kweryMail) throws IOException, EmailException {
        for (KweryMailAttachment kweryMailAttachment : kweryMail.getAttachments()) {
            InputStream byteArrayInputStream = new ByteArrayInputStream(kweryMailAttachment.getContent().getBytes("UTF-8"));
            DataSource datasource = new ByteArrayDataSource(byteArrayInputStream, "text/csv");
            multiPartEmail.attach(datasource, kweryMailAttachment.getName(), kweryMailAttachment.getDescription());
        }
    }
}
