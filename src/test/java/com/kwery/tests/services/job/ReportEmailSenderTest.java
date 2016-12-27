package com.kwery.tests.services.job;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.JsonToHtmlTableConvertor;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderTest extends RepoDashTestBase {
    @Test
    public void test() throws IOException {
        JobModel jobModel = new JobModel();
        jobModel.setTitle("Test Report");
        jobModel.setSqlQueries(new HashSet<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com", "moo@goo.com"));

        SqlQueryModel sqlQueryModel0 = new SqlQueryModel();
        sqlQueryModel0.setTitle("Select Authors");
        jobModel.getSqlQueries().add(sqlQueryModel0);

        SqlQueryModel sqlQueryModel1 = new SqlQueryModel();
        sqlQueryModel1.setTitle("Select Books");
        jobModel.getSqlQueries().add(sqlQueryModel1);

        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016

        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());

        SqlQueryExecutionModel sqlQueryExecutionModel0 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel0.setResult(TestUtil.toJson(ImmutableList.of(
                    ImmutableList.of("author"),
                    ImmutableList.of("peter thiel")
                )
        ));
        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);
        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel0);

        SqlQueryExecutionModel sqlQueryExecutionModel1 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel1.setResult(TestUtil.toJson(ImmutableList.of(
                ImmutableList.of("book"),
                ImmutableList.of("zero to one")
                )
        ));
        sqlQueryExecutionModel1.setSqlQuery(sqlQueryModel1);
        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel1);

        getInstance(ReportEmailSender.class).send(jobExecutionModel);

        String expectedSubject = "Thu Dec 22 2016 21:29 - Test Report";

        MailService mailService = getInstance(MailService.class);
        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();

        assertThat(mail.getSubject(), is(expectedSubject));

        List<String> expectedBody = new LinkedList<>();

        expectedBody.add("<h1>" + sqlQueryModel0.getTitle() + "</h1>");

        JsonToHtmlTableConvertor jsonToHtmlTableConvertor = getInstance(JsonToHtmlTableConvertor.class);
        expectedBody.add(jsonToHtmlTableConvertor.convert(sqlQueryExecutionModel0.getResult()));

        expectedBody.add("<h1>" + sqlQueryModel1.getTitle() + "</h1>");
        expectedBody.add(jsonToHtmlTableConvertor.convert(sqlQueryExecutionModel1.getResult()));

        assertThat(mail.getBodyHtml(), is(String.join("", expectedBody)));
    }
}
