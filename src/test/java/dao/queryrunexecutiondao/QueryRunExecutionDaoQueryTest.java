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
import util.TestUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class QueryRunExecutionDaoQueryTest extends RepoDashDaoTestBase {
    protected QueryRunExecutionDao queryRunExecutionDao;
    protected QueryRun queryRun;
    protected QueryRunExecution queryRunExecution;

    @Before
    public void setUpQueryRunExecutionDaoQueryTest() {
        Datasource datasource = TestUtil.datasource();
        getInstance(DatasourceDao.class).save(datasource);

        queryRun = TestUtil.queryRun();
        queryRun.setDatasource(datasource);
        getInstance(QueryRunDao.class).save(queryRun);

        queryRunExecutionDao = getInstance(QueryRunExecutionDao.class);

        queryRunExecution = TestUtil.queryRunExecution();
        queryRunExecution.setQueryRun(queryRun);

        queryRunExecutionDao.save(queryRunExecution);
    }

    @Test
    public void testGetByExecutionId() {
        QueryRunExecution fromDb = queryRunExecutionDao.getByExecutionId(queryRunExecution.getExecutionId());
        assertThat(fromDb, notNullValue());
        assertThat(fromDb.getId(), is(queryRunExecution.getId()));
    }

    @Test
    public void testGetById() {
        QueryRunExecution fromDb = queryRunExecutionDao.getById(queryRunExecution.getId());
        assertThat(fromDb, notNullValue());
    }

    @Test
    public void testByQueryRunId() {
        assertThat(queryRunExecutionDao.getById(queryRunExecution.getQueryRun().getId()), notNullValue());
        assertThat(queryRunExecutionDao.getById(queryRunExecution.getQueryRun().getId() + 100), nullValue());
    }
}
