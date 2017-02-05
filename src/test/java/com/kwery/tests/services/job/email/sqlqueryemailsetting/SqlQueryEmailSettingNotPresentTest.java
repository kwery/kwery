package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryEmailSettingNotPresentTest extends AbstractSqlQueryEmailSettingTest {
    @Test
    public void test() throws Exception {
        reportEmailSender.send(jobExecutionModel);
        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getHtmlContent(), notNullValue());
        assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(false));
    }
}
