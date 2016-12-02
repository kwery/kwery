package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.SqlQueryExecutionModel.Status.FAILURE;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoPersistTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected SqlQueryExecutionModel sqlQueryExecution;

    @Before
    public void setUpQueryRunExecutionDaoTest() {
        Datasource datasource = datasourceWithoutId();
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

        SqlQueryExecutionModel updated = sqlQueryExecutionDao.getByExecutionId(sqlQueryExecution.getExecutionId());
        updated.setExecutionEnd(100l);
        updated.setStatus(FAILURE);

        sqlQueryExecutionDao.save(updated);

        assertThat(updated.getId(), is(sqlQueryExecution.getId()));

        SqlQueryExecutionModel fromDb = sqlQueryExecutionDao.getById(updated.getId());

        assertThat(fromDb.getExecutionStart(), is(sqlQueryExecution.getExecutionStart()));
        assertThat(fromDb.getExecutionEnd(), is(100l));
        assertThat(fromDb.getStatus(), is(FAILURE));
    }
}
