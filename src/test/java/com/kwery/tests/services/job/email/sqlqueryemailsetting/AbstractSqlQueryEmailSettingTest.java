package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.ReportEmailSender;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.tests.util.WiserRule;
import org.junit.Before;
import org.junit.Rule;

import java.util.HashSet;
import java.util.LinkedList;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;

public class AbstractSqlQueryEmailSettingTest extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    SqlQueryModel sqlQueryModel0;
    JobExecutionModel jobExecutionModel;
    SqlQueryExecutionModel sqlQueryExecutionModel0;
    ReportEmailSender reportEmailSender;

    @Before
    public void setUp() {
        JobModel jobModel = new JobModel();
        jobModel.setTitle("Test Report");
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com"));

        sqlQueryModel0 = new SqlQueryModel();
        sqlQueryModel0.setId(1);
        sqlQueryModel0.setTitle("Select Authors");
        jobModel.getSqlQueries().add(sqlQueryModel0);

        jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016

        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());

        sqlQueryExecutionModel0 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel0.setId(1);
        sqlQueryExecutionModel0.setResult(TestUtil.toJson(ImmutableList.of(
                ImmutableList.of("author"),
                ImmutableList.of("peter thiel")
                )
        ));
        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);
        sqlQueryExecutionModel0.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel0);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
        emailConfigurationDbSet(wiserRule.emailConfiguration());

        reportEmailSender = getInstance(ReportEmailSender.class);
    }
}
