package com.kwery.tests.services.job.email.withoutcontent;

import com.kwery.services.job.ReportEmailSender;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ReportEmailSenderEmptyReportEmailRuleFalseTest extends AbstractReportEmailWithoutContentSender {
    @Test
    public void test() throws Exception {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);

        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getHtmlContent(), notNullValue());
        assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(false));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
