package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryEmailSettingModel.SQL_QUERY_EMAIL_SETTING_TABLE;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;

public class JobDaoDeleteJobWithDependentsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;

    @Before
    public void setUpJobDaoDeleteJobWithDependentsTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setChildJobs(new HashSet<>());

        jobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));

        JobModel dependentJobModel = jobModelWithoutDependents();
        dependentJobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));

        jobModel.getChildJobs().add(dependentJobModel);

        jobDbSetUp(ImmutableList.of(jobModel, dependentJobModel));
        jobDependentDbSetUp(jobModel);

        jobEmailDbSetUp(jobModel);
        jobEmailDbSetUp(dependentJobModel);

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel0 = sqlQueryModel();
        sqlQueryModel0.setDatasource(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);
        jobModel.setSqlQueries(ImmutableList.of(sqlQueryModel0));
        jobSqlQueryDbSetUp(jobModel);

        SqlQueryEmailSettingModel sqlQueryEmailSettingModel0 = sqlQueryEmailSettingModel();
        sqlQueryModel0.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel0);
        sqlQueryEmailSettingDbSetUp(sqlQueryModel0);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel();
        sqlQueryModel1.setDatasource(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);
        dependentJobModel.setSqlQueries(ImmutableList.of(sqlQueryModel1));
        jobSqlQueryDbSetUp(dependentJobModel);

        SqlQueryEmailSettingModel sqlQueryEmailSettingModel1 = sqlQueryEmailSettingModel();
        sqlQueryModel1.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel1);
        sqlQueryEmailSettingDbSetUp(sqlQueryModel1);

        JobExecutionModel jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionDbSetUp(jobExecutionModel);

        SqlQueryExecutionModel sqlQueryExecutionModel0 = sqlQueryExecutionModel();
        sqlQueryExecutionModel0.setJobExecutionModel(jobExecutionModel);
        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);

        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel0);

        SqlQueryExecutionModel sqlQueryExecutionModel1 = sqlQueryExecutionModel();
        sqlQueryExecutionModel1.setJobExecutionModel(jobExecutionModel);
        sqlQueryExecutionModel1.setSqlQuery(sqlQueryModel1);

        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel1);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        jobDao.delete(jobModel.getId());

        new DbTableAsserterBuilder(JOB_TABLE, DbUtil.jobTable(new ArrayList<>())).build().assertTable();
        new DbTableAsserterBuilder(JOB_CHILDREN_TABLE, DbUtil.jobDependentTable(null)).build().assertTable();
        new DbTableAsserterBuilder(JOB_EMAIL_TABLE, DbUtil.jobEmailTable(null)).build().assertTable();
        new DbTableAsserterBuilder(JOB_SQL_QUERY_TABLE, DbUtil.jobSqlQueryTable(null)).columnsToIgnore(JOB_SQL_QUERY_TABLE_UI_ORDER_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(SQL_QUERY_TABLE, DbUtil.sqlQueryTable(new ArrayList<>())).build().assertTable();
        new DbTableAsserterBuilder(JobExecutionModel.TABLE, DbUtil.jobExecutionTable(null)).build().assertTable();
        new DbTableAsserterBuilder(SqlQueryExecutionModel.TABLE, DbUtil.sqlQueryExecutionTable(null)).build().assertTable();
        new DbTableAsserterBuilder(SQL_QUERY_EMAIL_SETTING_TABLE, sqlQueryEmailSettingTable(null)).build().assertTable();
    }
}
