package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected SqlQueryExecution sqlQueryExecution;

    @Before
    public void setUpQueryRunExecutionDaoQueryTest() {
        Datasource datasource = TestUtil.datasource();
        getInstance(DatasourceDao.class).save(datasource);

        sqlQuery = TestUtil.queryRun();
        sqlQuery.setDatasource(datasource);
        getInstance(SqlQueryDao.class).save(sqlQuery);

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);

        sqlQueryExecution = TestUtil.queryRunExecution();
        sqlQueryExecution.setSqlQuery(sqlQuery);

        sqlQueryExecutionDao.save(sqlQueryExecution);
    }

    @Test
    public void testGetByExecutionId() {
        SqlQueryExecution fromDb = sqlQueryExecutionDao.getByExecutionId(sqlQueryExecution.getExecutionId());
        assertThat(fromDb, notNullValue());
        assertThat(fromDb.getId(), is(sqlQueryExecution.getId()));
    }

    @Test
    public void testGetById() {
        SqlQueryExecution fromDb = sqlQueryExecutionDao.getById(sqlQueryExecution.getId());
        assertThat(fromDb, notNullValue());
    }

    @Test
    public void testByQueryRunId() {
        assertThat(sqlQueryExecutionDao.getById(sqlQueryExecution.getSqlQuery().getId()), notNullValue());
        assertThat(sqlQueryExecutionDao.getById(sqlQueryExecution.getSqlQuery().getId() + 100), nullValue());
    }
}
