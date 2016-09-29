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

import static models.SqlQueryExecution.Status.FAILURE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;
import static util.TestUtil.queryRunExecution;

public class SqlQueryExecutionDaoPersistTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQuery sqlQuery;
    protected SqlQueryExecution sqlQueryExecution;

    @Before
    public void setUpQueryRunExecutionDaoTest() {
        Datasource datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);

        sqlQuery = queryRun();
        sqlQuery.setDatasource(datasource);
        getInstance(SqlQueryDao.class).save(sqlQuery);

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);

        sqlQueryExecution = queryRunExecution();
        sqlQueryExecution.setSqlQuery(sqlQuery);
    }

    @Test
    public void testPersist() {
        sqlQueryExecutionDao.save(sqlQueryExecution);

        assertThat(sqlQueryExecution.getId(), notNullValue());
        assertThat(sqlQueryExecution.getId(), greaterThan(0));
    }

    @Test
    public void testUpdate() {
        sqlQueryExecutionDao.save(sqlQueryExecution);

        SqlQueryExecution updated = sqlQueryExecutionDao.getByExecutionId(sqlQueryExecution.getExecutionId());
        updated.setExecutionEnd(100l);
        updated.setStatus(FAILURE);

        sqlQueryExecutionDao.update(updated);

        assertThat(updated.getId(), is(sqlQueryExecution.getId()));

        SqlQueryExecution fromDb = sqlQueryExecutionDao.getById(updated.getId());

        assertThat(fromDb.getExecutionStart(), is(sqlQueryExecution.getExecutionStart()));
        assertThat(fromDb.getExecutionEnd(), is(100l));
        assertThat(fromDb.getStatus(), is(FAILURE));
    }
}
