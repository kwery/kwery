package com.kwery.tests.services.job.email.withoutcontent;

import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.mail.MailService;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class ReportEmailSenderEmptyReportEmailRuleTrueTest extends AbstractReportEmailWithoutContentSender {
    @Test
    public void test() {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);
        MailService mailService = getInstance(MailService.class);
        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();
        assertThat(mail, nullValue());
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return true;
    }
}
