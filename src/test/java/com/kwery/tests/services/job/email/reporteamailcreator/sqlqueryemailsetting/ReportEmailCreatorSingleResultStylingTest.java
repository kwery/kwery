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
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(Parameterized.class)
public class ReportEmailCreatorSingleResultStylingTest extends RepoDashTestBase {
    protected boolean singleResultStyling;

    public ReportEmailCreatorSingleResultStylingTest(boolean singleResultStyling) {
        this.singleResultStyling = singleResultStyling;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

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

        SqlQueryEmailSettingModel sqlQueryEmailSettingModel = new SqlQueryEmailSettingModel();
        sqlQueryEmailSettingModel.setIncludeInEmailBody(true);
        sqlQueryEmailSettingModel.setIncludeInEmailAttachment(true);
        sqlQueryEmailSettingModel.setSingleResultStyling(singleResultStyling);
        sqlQueryModel0.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel);

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

    @Test
    public void test() throws Exception {
        KweryMail kweryMail = reportEmailCreator.create(jobExecutionModel, new LinkedList<>());
        String html = kweryMail.getBodyHtml();
        Document doc = Jsoup.parse(html);

        if (singleResultStyling) {
            Element table = doc.select(".report-content-t").first();
            assertThat(table.attr("style"), containsString("border: none;"));
            assertThat(table.attr("style"), not(containsString("border: 1px solid black;")));

            Element columnLabel = doc.select(".report-label-0-t").first();
            assertThat(columnLabel, nullValue());

            Element row = doc.select(".section-0-row-0-t").first();
            assertThat(row.attr("style"), containsString("border: none;"));
            assertThat(row.attr("style"), not(containsString("border: 1px solid black;")));

            Element col = doc.select(".section-0-row-0-col-0-t").first();
            assertThat(col.attr("style"), containsString("border: none; font-size: x-large"));
            assertThat(col.attr("style"), not(containsString("border: 1px solid black;")));
        } else {
            Element table = doc.select(".report-content-t").first();
            assertThat(table.attr("style"), containsString("border: 1px solid black"));
            assertThat(table.attr("style"), not(containsString("border: none;")));

            Element columnLabel = doc.select(".report-label-0-t").first();
            assertThat(columnLabel, notNullValue());

            Element row = doc.select(".section-0-row-0-t").first();
            assertThat(row.attr("style"), containsString("border: 1px solid black;"));
            assertThat(row.attr("style"), not(containsString("border: none;")));

            Element col = doc.select(".section-0-row-0-col-0-t").first();
            assertThat(col.attr("style"), containsString("border: 1px solid black;"));
            assertThat(col.attr("style"), not(containsString("border: none; font-size: x-large;")));
        }
    }
}
