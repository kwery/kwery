package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.RepoDashDaoTestBase;

import static com.kwery.models.SqlQueryExecution.Status.FAILURE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.queryRun;
import static com.kwery.tests.util.TestUtil.queryRunExecution;

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
