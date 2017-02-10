package com.kwery.tests.services.job.email.withcontent;

import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.scheduler.JsonToHtmlTableConverter;
import com.kwery.services.scheduler.JsonToHtmlTableConverterFactory;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.List;

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

        JsonToHtmlTableConverterFactory jsonToHtmlTableConverterFactory = getInstance(JsonToHtmlTableConverterFactory.class);
        JsonToHtmlTableConverter converter0 = jsonToHtmlTableConverterFactory.create(sqlQueryExecutionModel0.getResult());
        expectedBody.add(converter0.convert());

        expectedBody.add("<h1>" + sqlQueryModel1.getTitle() + "</h1>");
        JsonToHtmlTableConverter converter1 = jsonToHtmlTableConverterFactory.create(sqlQueryExecutionModel1.getResult());
        expectedBody.add(converter1.convert());

        expectedBody.add("<h1>" + sqlQueryModel2.getTitle() + "</h1>");
        expectedBody.add("<div></div>");

        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getHtmlContent(), is(String.join("", expectedBody)));
        assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(false));
        assertThat(mimeMessageParser.getSubject(), is(expectedSubject));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
