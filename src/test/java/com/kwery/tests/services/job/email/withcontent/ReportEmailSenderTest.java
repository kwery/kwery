package com.kwery.tests.services.job.email.withcontent;

import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.JsonToHtmlTableConverter;
import com.kwery.services.scheduler.JsonToHtmlTableConverterFactory;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderTest extends AbstractReportEmailWithContentSender {
    @Test
    public void test() throws IOException {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);

        String expectedSubject = "Thu Dec 22 2016 21:29 - Test Report";

        MailService mailService = getInstance(MailService.class);
        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();

        assertThat(mail.getSubject(), is(expectedSubject));

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

        assertThat(mail.getBodyHtml(), is(String.join("", expectedBody)));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
