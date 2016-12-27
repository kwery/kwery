package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;

public class SqlQueryExecutionDaoUpdateTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionModel sqlQueryExecutionModel;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected JobExecutionModel jobExecutionModel0;
    private SqlQueryModel sqlQuery;

    @Before
    public void setUpSqlQueryExecutionDaoUpdateTest() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQuery = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQuery);

        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        JobExecutionModel jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);

        sqlQueryExecutionModel = sqlQueryExecutionModel();
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);

        jobExecutionModel0 = jobExecutionModel();
        jobExecutionModel0.setJobModel(jobModel);

        jobExecutionDbSetUp(jobExecutionModel0);

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws Exception {
        SqlQueryExecutionModel updated = sqlQueryExecutionModelWithoutId();
        updated.setId(sqlQueryExecutionModel.getId());
        updated.setJobExecutionModel(jobExecutionModel0);
        updated.setSqlQuery(sqlQuery);

        DozerBeanMapper mapper = new DozerBeanMapper();
        SqlQueryExecutionModel expected = mapper.map(updated, SqlQueryExecutionModel.class);

        sqlQueryExecutionDao.save(updated);

        assertDbState(SqlQueryExecutionModel.TABLE, sqlQueryExecutionTable(expected));
    }
}
