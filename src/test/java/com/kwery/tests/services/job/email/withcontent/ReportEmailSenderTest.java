package com.kwery.tests.services.job.email.withcontent;

import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.tests.services.job.email.EmailHtmlTestUtil;
import com.kwery.tests.util.TestUtil;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

        String html = mimeMessageParser.getHtmlContent();

        assertTitle(0, sqlQueryModel0.getTitle(), html);

        Document doc = Jsoup.parse(html);
        Elements headers = doc.select(".report-content-t th");
        assertThat(headers.get(0).text(), is("author"));
        Elements columns = doc.select(".report-content-t td");
        assertThat(columns.get(0).text(), is("peter thiel"));

        assertTitle(1, sqlQueryModel1.getTitle(), html);
        assertThat(headers.get(1).text(), is("book"));
        assertThat(columns.get(1).text(), is("zero to one"));

        assertTitle(2, sqlQueryModel2.getTitle(), html);
        assertThat(doc.select(".report-content-t td").size(), is(2));

        assertTitle(3, sqlQueryModel3.getTitle(), html);

        EmailHtmlTestUtil.assertReportFooter(html);

        String largeReportWarning = doc.select(".large-report-warning-t").get(0).text();
        assertThat(largeReportWarning, is("Report too large to display, please download attachment and view."));

        String attachmentSkippedWarning = doc.select(".large-attachment-warning-t").get(0).text();
        assertThat(attachmentSkippedWarning, is("P.S. Some reports were not attached as the files were too large."));

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

    public void assertTitle(int index, String expected, String html) {
        Document doc = Jsoup.parse(html);
        Element titleElement = doc.select(".report-title-t").get(index);
        assertThat(titleElement.text(), is(expected));
    }

}
