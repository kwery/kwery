package com.kwery.tests.services.job.email.reporteamailcreator.withoutcontent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.tests.util.WiserRule;
import com.kwery.conf.KweryDirectory;
import org.junit.Before;
import org.junit.Rule;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;

public abstract class AbstractReportEmailWithoutContentSender extends RepoDashTestBase {
    SqlQueryModel sqlQueryModel0;
    JobExecutionModel jobExecutionModel;
    SqlQueryExecutionModel sqlQueryExecutionModel0;

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

        jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016

        jobExecutionModel.setSqlQueryExecutionModels(new LinkedHashSet<>());

        sqlQueryExecutionModel0 = TestUtil.sqlQueryExecutionModel();
        sqlQueryExecutionModel0.setId(1);

        KweryDirectory kweryDirectory = getInstance(KweryDirectory.class);
        File file = kweryDirectory.createFile();

        String csv = String.join(System.lineSeparator(), "author") + System.lineSeparator();

        Files.write(Paths.get(file.getPath()), csv.getBytes(), StandardOpenOption.APPEND);

        sqlQueryExecutionModel0.setResultFileName(file.getName());

        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);
        sqlQueryExecutionModel0.setJobExecutionModel(jobExecutionModel);

        sqlQueryExecutionModel0.setStatus(SqlQueryExecutionModel.Status.SUCCESS);

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel0);
    }

    public abstract boolean getEmptyReportEmailRule();
}
