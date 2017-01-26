package com.kwery.tests.services.job.email.withcontent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;

import java.util.HashSet;
import java.util.LinkedList;

import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;

public abstract class AbstractReportEmailWithContentSender extends RepoDashTestBase {
    SqlQueryModel sqlQueryModel0;
    SqlQueryModel sqlQueryModel1;
    SqlQueryModel sqlQueryModel2;
    JobExecutionModel jobExecutionModel;
    SqlQueryExecutionModel sqlQueryExecutionModel0;
    SqlQueryExecutionModel sqlQueryExecutionModel1;

    @Before
    public void setUp() {
        JobModel jobModel = new JobModel();
        jobModel.setTitle("Test Report");
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com", "moo@goo.com"));
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(getEmptyReportEmailRule())));

        sqlQueryModel0 = new SqlQueryModel();
        sqlQueryModel0.setId(1);
        sqlQueryModel0.setTitle("Select Authors");
        jobModel.getSqlQueries().add(sqlQueryModel0);

        sqlQueryModel1 = new SqlQueryModel();
        sqlQueryModel1.setId(2);
        sqlQueryModel1.setTitle("Select Books");
        jobModel.getSqlQueries().add(sqlQueryModel1);

        sqlQueryModel2 = new SqlQueryModel();
        sqlQueryModel2.setId(3);
        sqlQueryModel2.setTitle("Insert Books");
        jobModel.getSqlQueries().add(sqlQueryModel2);

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

        sqlQueryExecutionModel1 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel1.setId(2);
        sqlQueryExecutionModel1.setResult(TestUtil.toJson(ImmutableList.of(
                ImmutableList.of("book"),
                ImmutableList.of("zero to one")
                )
        ));
        sqlQueryExecutionModel1.setSqlQuery(sqlQueryModel1);
        sqlQueryExecutionModel1.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel1);

        SqlQueryExecutionModel sqlQueryExecutionModel2 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel2.setId(3);
        sqlQueryExecutionModel2.setSqlQuery(sqlQueryModel2);
        jobExecutionModel.getSqlQueryExecutionModels().add(sqlQueryExecutionModel2);
    }

    public abstract boolean getEmptyReportEmailRule();
}
