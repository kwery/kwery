package com.kwery.custom;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.KweryMailAttachment;
import com.kwery.services.mail.smtp.SmtpService;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.commonsmail.CommonsmailHelper;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.activation.FileDataSource;
import java.io.IOException;

public class KweryPostofficeImpl implements Postoffice {
    private final CommonsmailHelper commonsmailHelper;
    private final SmtpService smtpService;
    private final EmailConfigurationDao emailConfigurationDao;

    @Inject
    public KweryPostofficeImpl(CommonsmailHelper commonsmailHelper, SmtpService smtpService, EmailConfigurationDao emailConfigurationDao) {
        this.commonsmailHelper = commonsmailHelper;
        this.smtpService = smtpService;
        this.emailConfigurationDao = emailConfigurationDao;
    }

    @Override
    public void send(Mail mail) throws Exception {
        EmailConfiguration emailConfiguration = emailConfigurationDao.get();
        mail.setFrom(emailConfiguration.getFrom());

        if (!Strings.nullToEmpty(emailConfiguration.getBcc()).trim().equals("")) {
            for (String email : emailConfiguration.getBcc().split(",")) {
                mail.addBcc(email);
            }
        }

        if (!Strings.nullToEmpty(emailConfiguration.getReplyTo()).trim().equals("")) {
            mail.getReplyTo().clear();
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

        //Send mail
        multiPartEmail.send();
    }

    protected void attachAttachments(MultiPartEmail multiPartEmail, KweryMail kweryMail) throws IOException, EmailException {
        for (KweryMailAttachment kweryMailAttachment : kweryMail.getAttachments()) {
            FileDataSource ds = new FileDataSource(kweryMailAttachment.getFile());
            multiPartEmail.attach(ds, kweryMailAttachment.getName(), kweryMailAttachment.getDescription());
        }
    }
}
