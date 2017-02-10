package com.kwery.tests.services.job.email.reportfailurealert;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportFailureAlertEmailSenderWithoutBodyTest extends ReportFailureAlertEmailSenderAbstractTest {
    @Test
    public void test() throws Exception {
        emailSender.send(jobExecutionModel);

        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getSubject(), is(expectedSubject()));
        assertThat(mimeMessageParser.getHtmlContent(), is(" "));
    }
}
