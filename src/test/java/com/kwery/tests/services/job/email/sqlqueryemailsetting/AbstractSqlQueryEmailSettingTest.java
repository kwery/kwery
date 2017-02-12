package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import com.google.common.collect.ImmutableSet;
import com.kwery.models.*;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.tests.util.WiserRule;
import com.kwery.utils.KweryDirectory;
import org.apache.commons.mail.util.MimeMessageParser;
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

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
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
        jobModel.setTitle("Test Report");
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com"));

        sqlQueryModel0 = TestUtil.sqlQueryModel();
        sqlQueryModel0.setId(1);
        sqlQueryModel0.setTitle("Select Authors");
        jobModel.getSqlQueries().add(sqlQueryModel0);

        jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

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

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel0);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
        emailConfigurationDbSet(wiserRule.emailConfiguration());

        reportEmailSender = getInstance(ReportEmailSender.class);
    }

    public void setUp(boolean includeInEmail, boolean includeInAttachment) {
        SqlQueryEmailSettingModel model = new SqlQueryEmailSettingModel();
        model.setIncludeInEmailAttachment(includeInEmail);
        model.setIncludeInEmailBody(includeInAttachment);
        sqlQueryModel0.setSqlQueryEmailSettingModel(model);
    }

    public void assertEmail(boolean emptyBody, boolean emptyAttachment) throws Exception {
        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();

        if (emptyBody) {
            assertThat(mimeMessageParser.getHtmlContent(), is(" "));
        } else {
            assertThat(mimeMessageParser.getHtmlContent(), not(" "));
        }

        assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(emptyAttachment));
    }

    public void setEmailSetting(boolean includeInBody, boolean includeInAttachment) {
        SqlQueryEmailSettingModel model = new SqlQueryEmailSettingModel();
        model.setIncludeInEmailBody(includeInBody);
        model.setIncludeInEmailAttachment(includeInAttachment);
        sqlQueryModel0.setSqlQueryEmailSettingModel(model);
    }
}
