package dao.queryrunexecutiondao;

import models.QueryRunExecution;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class QueryRunExecutionDaoQueryTest extends QueryRunExecutionDaoPersistTest {
    @Before
    public void setUpQueryRunExecutionDaoQueryTest() {
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
