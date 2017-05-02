package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import com.google.common.collect.ImmutableSet;
import com.kwery.conf.KweryDirectory;
import com.kwery.models.*;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.tests.util.WiserRule;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Rule;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.LinkedList;

import static com.jayway.jsonassert.impl.matcher.IsEmptyCollection.empty;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.services.job.email.EmailHtmlTestUtil.assertReportFooter;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AbstractSqlQueryEmailSettingTest extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    SqlQueryModel sqlQueryModel0;
    JobExecutionModel jobExecutionModel;
    SqlQueryExecutionModel sqlQueryExecutionModel0;
    ReportEmailSender reportEmailSender;

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

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
        emailConfigurationDbSet(wiserRule.emailConfiguration());

        reportEmailSender = getInstance(ReportEmailSender.class);
    }

    public void assertEmail(boolean emptyBody, boolean emptyAttachment) throws Exception {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());

        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();

        String htmlContent = mimeMessageParser.getHtmlContent();
        assertReportFooter(htmlContent);

        if (emptyBody) {
            assertSection(htmlContent, false);
        } else {
            assertSection(htmlContent, true);
        }

        assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(emptyAttachment));
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
