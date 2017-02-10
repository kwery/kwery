package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import com.kwery.models.SqlQueryEmailSettingModel;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SqlQueryEmailSettingTrueTest extends AbstractSqlQueryEmailSettingTest {
    @Before
    public void setUp() {
        super.setUp();
        SqlQueryEmailSettingModel model = new SqlQueryEmailSettingModel();
        model.setIncludeInEmailAttachment(true);
        model.setIncludeInEmailBody(true);
        sqlQueryModel0.setSqlQueryEmailSettingModel(model);
        setEmailSetting(true, true);
    }

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
