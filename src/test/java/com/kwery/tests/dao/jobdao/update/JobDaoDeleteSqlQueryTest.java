package com.kwery.tests.dao.jobdao.update;

import com.kwery.dao.JobDao;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;

public class JobDaoDeleteSqlQueryTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;
    private long created;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        datasource = datasource();
        datasourceDbSetup(datasource);

        for (int i = 0; i < 2; ++i) {
            jobModel.getSqlQueries().add(sqlQueryModel(datasource));
        }

        sqlQueryDbSetUp(jobModel.getSqlQueries());

        jobSqlQueryDbSetUp(jobModel);

        JobExecutionModel jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);

        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModel();
            sqlQueryModel.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel);
            sqlQueryEmailSettingDbSetUp(sqlQueryModel);

            SqlQueryExecutionModel sqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
            sqlQueryExecutionModel.setSqlQuery(sqlQueryModel);
            sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
            DbUtil.sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);
        }

        created = jobModel.getCreated();

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        jobModel.getSqlQueries().remove(0);
        jobDao.save(jobModel);
        new DbTableAsserterBuilder(SQL_QUERY_TABLE, sqlQueryTable(jobModel.getSqlQueries().get(0))).build().assertTable();
    }
}
