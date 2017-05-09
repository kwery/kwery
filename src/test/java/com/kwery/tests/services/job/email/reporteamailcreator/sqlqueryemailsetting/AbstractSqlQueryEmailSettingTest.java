package com.kwery.tests.services.job.email.reporteamailcreator.sqlqueryemailsetting;

import com.google.common.collect.ImmutableSet;
import com.kwery.conf.KweryDirectory;
import com.kwery.models.*;
import com.kwery.services.job.ReportEmailCreator;
import com.kwery.services.mail.KweryMail;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.LinkedList;

import static com.jayway.jsonassert.impl.matcher.IsEmptyCollection.empty;
import static com.kwery.tests.services.job.email.EmailHtmlTestUtil.assertReportFooter;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AbstractSqlQueryEmailSettingTest extends RepoDashTestBase {
    SqlQueryModel sqlQueryModel0;
    JobExecutionModel jobExecutionModel;
    SqlQueryExecutionModel sqlQueryExecutionModel0;
    ReportEmailCreator reportEmailCreator;

    @Before
    public void setUp() throws IOException {
        JobModel jobModel = new JobModel();
        jobModel.setId(1);

        jobModel.setTitle("Test Report");
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com"));

        sqlQueryModel0 = TestUtil.sqlQueryModel();
        sqlQueryModel0.setId(1);
        sqlQueryModel0.setTitle("Select Authors");
        jobModel.getSqlQueries().add(sqlQueryModel0);

        jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionModel.setExecutionId("foobarmoo");
        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016

        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());

        KweryDirectory kweryDirectory = getInstance(KweryDirectory.class);
        File file = kweryDirectory.createFile();

        String csv = String.join(System.lineSeparator(), "author", "peter thiel") + System.lineSeparator();

        Files.write(Paths.get(file.getPath()), csv.getBytes(), StandardOpenOption.APPEND);

        sqlQueryExecutionModel0 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel0.setId(1);
        sqlQueryExecutionModel0.setResultFileName(file.getName());
        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);
        sqlQueryExecutionModel0.setJobExecutionModel(jobExecutionModel);
        sqlQueryExecutionModel0.setStatus(SqlQueryExecutionModel.Status.SUCCESS);

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel0);

        reportEmailCreator = getInstance(ReportEmailCreator.class);
    }

    public void assertEmail(boolean emptyBody, boolean emptyAttachment, KweryMail kweryMail) throws Exception {
        String htmlContent = kweryMail.getBodyHtml();
        assertReportFooter(htmlContent);

        if (emptyBody) {
            assertSection(htmlContent, false);
        } else {
            assertSection(htmlContent, true);
        }

        assertThat(kweryMail.getAttachments().isEmpty(), is(emptyAttachment));
    }

    public void setEmailSetting(boolean includeInBody, boolean includeInAttachment) {
        SqlQueryEmailSettingModel model = new SqlQueryEmailSettingModel();
        model.setIncludeInEmailBody(includeInBody);
        model.setIncludeInEmailAttachment(includeInAttachment);
        sqlQueryModel0.setSqlQueryEmailSettingModel(model);
    }

    public void setEmailSetting(boolean includeInBody, boolean includeInAttachment, boolean ignoreLabel) {
        setEmailSetting(includeInBody, includeInAttachment);
        sqlQueryModel0.getSqlQueryEmailSettingModel().setSingleResultStyling(ignoreLabel);
    }

    protected void assertSection(String html, boolean present) {
        Document doc = Jsoup.parse(html);
        Elements headers = doc.select(".report-content-t th");

        if (present) {
            assertThat(headers.size(), greaterThanOrEqualTo(1));
        } else {
            assertThat(headers, empty());
        }
    }
}
