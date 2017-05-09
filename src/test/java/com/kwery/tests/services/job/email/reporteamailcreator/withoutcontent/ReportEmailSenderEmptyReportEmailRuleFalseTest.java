package com.kwery.tests.services.job.email.reporteamailcreator.withoutcontent;

import com.kwery.services.job.ReportEmailCreator;
import com.kwery.services.mail.KweryMail;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import java.util.LinkedList;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ReportEmailSenderEmptyReportEmailRuleFalseTest extends AbstractReportEmailWithoutContentSender {
    @Test
    public void test() throws Exception {
        KweryMail kweryMail = getInstance(ReportEmailCreator.class).create(jobExecutionModel, new LinkedList<>());

        assertThat(kweryMail.getBodyHtml(), notNullValue());
        assertThat(kweryMail.getAttachments().isEmpty(), is(false));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
