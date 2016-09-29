package dao.sqlqueryexecutiondao;

import dao.DatasourceDao;
import dao.SqlQueryDao;
import dao.SqlQueryExecutionDao;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;
import util.TestUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQuery sqlQuery;
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
