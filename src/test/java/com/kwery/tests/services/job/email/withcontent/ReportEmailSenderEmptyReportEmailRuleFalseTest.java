package com.kwery.tests.services.job.email.withcontent;

import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.mail.MailService;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import java.util.LinkedList;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderEmptyReportEmailRuleFalseTest extends AbstractReportEmailWithContentSender {
    @Test
    public void test() throws Exception {
        getInstance(ReportEmailSender.class).send(jobExecutionModel, new LinkedList<>());
        assertMailPresent();
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
