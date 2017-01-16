package com.kwery.tests.services.job.email.withcontent;

import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.mail.MailService;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderEmptyReportEmailRuleTrueTest extends AbstractReportEmailWithContentSender {
    @Test
    public void test() {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);
        MailService mailService = getInstance(MailService.class);
        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();
        assertThat(mail, notNullValue());
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return true;
    }
}
