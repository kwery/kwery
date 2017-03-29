package com.kwery.tests.services.job.email.withcontent;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.tests.util.TestUtil;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderTest extends AbstractReportEmailWithContentSender {
    @Test
    public void test() throws Exception {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);

        String expectedSubject = "Test Report - Thu Dec 22 2016 21:29";

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());
        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();

        String actual = Joiner.on("").join(Splitter.on("\n").omitEmptyStrings().trimResults().splitToList(
                mimeMessageParser.getHtmlContent().replaceAll("\r\n", "\n"))
        );

        String expected = Joiner.on("").join(Splitter.on("\n").omitEmptyStrings().trimResults().splitToList(
                Resources.toString(Resources.getResource("email/expectedReport.html"), Charsets.UTF_8))
        );

        assertThat(actual, is(expected));

        assertThat(mimeMessageParser.getAttachmentList(), IsCollectionWithSize.hasSize(2));
        assertThat(mimeMessageParser.getSubject(), is(expectedSubject));

        DataSource dataSource0 = mimeMessageParser.findAttachmentByName("select-authors-thu-dec-22.csv");
        assertThat(TestUtil.toString(dataSource0).replaceAll("\r\n", "\n").trim(), is(TestUtil.toString(kweryDirectory.getFile(sqlQueryExecutionModel0.getResultFileName())).trim()));

        DataSource dataSource1 = mimeMessageParser.findAttachmentByName("select-books-thu-dec-22.csv");
        assertThat(TestUtil.toString(dataSource1).replaceAll("\r\n", "\n").trim(), is(TestUtil.toString(kweryDirectory.getFile(sqlQueryExecutionModel1.getResultFileName())).trim()));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
