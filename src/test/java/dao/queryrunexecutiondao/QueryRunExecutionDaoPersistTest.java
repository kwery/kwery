package dao.queryrunexecutiondao;

import dao.DatasourceDao;
import dao.QueryRunDao;
import dao.QueryRunExecutionDao;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import static models.QueryRunExecution.Status.FAILURE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;
import static util.TestUtil.queryRunExecution;

public class QueryRunExecutionDaoPersistTest extends RepoDashDaoTestBase {
    protected QueryRunExecutionDao queryRunExecutionDao;
    protected QueryRun queryRun;
    protected QueryRunExecution queryRunExecution;

    @Before
    public void setUpQueryRunExecutionDaoTest() {
        Datasource datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);

        queryRun = queryRun();
        queryRun.setDatasource(datasource);
        getInstance(QueryRunDao.class).save(queryRun);

        queryRunExecutionDao = getInstance(QueryRunExecutionDao.class);

        queryRunExecution = queryRunExecution();
        queryRunExecution.setQueryRun(queryRun);
    }

    @Test
    public void testPersist() {
        queryRunExecutionDao.save(queryRunExecution);

        assertThat(queryRunExecution.getId(), notNullValue());
        assertThat(queryRunExecution.getId(), greaterThan(0));
    }

    @Test
    public void testUpdate() {
        queryRunExecutionDao.save(queryRunExecution);

        QueryRunExecution updated = queryRunExecutionDao.getByExecutionId(queryRunExecution.getExecutionId());
        updated.setExecutionEnd(100l);
        updated.setStatus(FAILURE);

        queryRunExecutionDao.update(updated);

        assertThat(updated.getId(), is(queryRunExecution.getId()));

        QueryRunExecution fromDb = queryRunExecutionDao.getById(updated.getId());

        assertThat(fromDb.getExecutionStart(), is(queryRunExecution.getExecutionStart()));
        assertThat(fromDb.getExecutionEnd(), is(100l));
        assertThat(fromDb.getStatus(), is(FAILURE));
    }
}
