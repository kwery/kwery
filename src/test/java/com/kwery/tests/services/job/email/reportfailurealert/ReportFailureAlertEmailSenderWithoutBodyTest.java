package com.kwery.tests.services.job.email.reportfailurealert;

import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class ReportFailureAlertEmailSenderWithoutBodyTest extends ReportFailureAlertEmailSenderAbstractTest {
    @Test
    public void test() {
        emailSender.send(jobExecutionModel);

        KweryMail kweryMail = getInstance(KweryMail.class);
        kweryMail.setSubject(expectedSubject());

        MailService mailService = getInstance(MailService.class);
        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();

        assertThat(mail, theSameBeanAs(kweryMail));
    }
}
