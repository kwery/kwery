package com.kwery.tests.services.job.email.withcontent;

import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.scheduler.CsvToHtmlConverter;
import com.kwery.tests.util.TestUtil;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING_M;
import static com.kwery.tests.util.Messages.REPORTEMAILSENDER_ATTACHMENT_SKIPPED_M;
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

        List<String> expectedBody = new LinkedList<>();

        expectedBody.add("<h1>" + sqlQueryModel0.getTitle() + "</h1>");

        CsvToHtmlConverter converter0 = csvToHtmlConverterFactory.create(kweryDirectory.getFile(sqlQueryExecutionModel0.getResultFileName()));
        expectedBody.add(converter0.convert());

        expectedBody.add("<h1>" + sqlQueryModel1.getTitle() + "</h1>");
        CsvToHtmlConverter converter1 = csvToHtmlConverterFactory.create(kweryDirectory.getFile(sqlQueryExecutionModel1.getResultFileName()));
        expectedBody.add(converter1.convert());

        expectedBody.add("<h1>" + sqlQueryModel2.getTitle() + "</h1>");
        expectedBody.add("<div></div>");


        expectedBody.add("<h1>" + sqlQueryModel3.getTitle() + "</h1>");
        expectedBody.add(String.format("<p>%s</p>", JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING_M));
        expectedBody.add(String.format("<p style='color:red'>%s</p>", REPORTEMAILSENDER_ATTACHMENT_SKIPPED_M));

        expectedBody.add("<br><hr><p>Report generated using <a href='http://getkwery.com'>Kwery</a></p>");

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());
        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getHtmlContent(), is(String.join("", expectedBody)));
        assertThat(mimeMessageParser.getAttachmentList(), IsCollectionWithSize.hasSize(2));
        assertThat(mimeMessageParser.getSubject(), is(expectedSubject));

        DataSource dataSource0 = mimeMessageParser.findAttachmentByName("select-authors-thu-dec-22.csv");
        assertThat(TestUtil.toString(dataSource0).trim(), is(TestUtil.toString(kweryDirectory.getFile(sqlQueryExecutionModel0.getResultFileName())).trim()));

        DataSource dataSource1 = mimeMessageParser.findAttachmentByName("select-books-thu-dec-22.csv");
        assertThat(TestUtil.toString(dataSource1).trim(), is(TestUtil.toString(kweryDirectory.getFile(sqlQueryExecutionModel1.getResultFileName())).trim()));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
