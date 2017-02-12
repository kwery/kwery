package com.kwery.tests.services.job.email.withcontent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.CsvToHtmlConverterFactory;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.LinkedList;

import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public abstract class AbstractReportEmailWithContentSender extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    SqlQueryModel sqlQueryModel0;
    SqlQueryModel sqlQueryModel1;
    SqlQueryModel sqlQueryModel2;
    JobExecutionModel jobExecutionModel;
    SqlQueryExecutionModel sqlQueryExecutionModel0;
    SqlQueryExecutionModel sqlQueryExecutionModel1;
    CsvToHtmlConverterFactory csvToHtmlConverterFactory;
    KweryDirectory kweryDirectory;

    @Before
    public void setUp() throws Exception {
        JobModel jobModel = new JobModel();
        jobModel.setTitle("Test Report");
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com"));
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(getEmptyReportEmailRule())));

        sqlQueryModel0 = TestUtil.sqlQueryModel();
        sqlQueryModel0.setId(1);
        sqlQueryModel0.setTitle("Select Authors");
        jobModel.getSqlQueries().add(sqlQueryModel0);

        sqlQueryModel1 = TestUtil.sqlQueryModel();
        sqlQueryModel1.setId(2);
        sqlQueryModel1.setTitle("Select Books");
        jobModel.getSqlQueries().add(sqlQueryModel1);

        sqlQueryModel2 = TestUtil.sqlQueryModel();
        sqlQueryModel2.setId(3);
        sqlQueryModel2.setTitle("Insert Books");
        sqlQueryModel2.setQuery("insert into foo");
        jobModel.getSqlQueries().add(sqlQueryModel2);

        jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016

        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());

        sqlQueryExecutionModel0 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel0.setId(1);

        kweryDirectory = getInstance(KweryDirectory.class);
        File file0 = kweryDirectory.createFile();
        String csv = String.join(System.lineSeparator(), "author", "peter thiel") + System.lineSeparator();
        Files.write(Paths.get(file0.getPath()), csv.getBytes(), StandardOpenOption.APPEND);
        sqlQueryExecutionModel0.setResultFileName(file0.getName());
        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);
        sqlQueryExecutionModel0.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel0);

        sqlQueryExecutionModel1 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel1.setId(2);

        File file1 = kweryDirectory.createFile();
        csv = String.join(System.lineSeparator(), "book", "zero to one") + System.lineSeparator();
        Files.write(Paths.get(file0.getPath()), csv.getBytes(), StandardOpenOption.APPEND);

        sqlQueryExecutionModel1.setResultFileName(file1.getName());
        sqlQueryExecutionModel1.setSqlQuery(sqlQueryModel1);
        sqlQueryExecutionModel1.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel1);

        SqlQueryExecutionModel sqlQueryExecutionModel2 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel2.setId(3);
        sqlQueryExecutionModel2.setSqlQuery(sqlQueryModel2);
        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel2);

        csvToHtmlConverterFactory = getInstance(CsvToHtmlConverterFactory.class);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
        emailConfigurationDbSet(wiserRule.emailConfiguration());
    }

    public void assertMailPresent() throws Exception {
        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getHtmlContent(), notNullValue());
        assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(false));
    }

    public abstract boolean getEmptyReportEmailRule();
}
