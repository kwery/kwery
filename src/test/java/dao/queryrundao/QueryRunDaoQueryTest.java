package dao.queryrundao;

import dao.DatasourceDao;
import dao.QueryRunDao;
import models.Datasource;
import models.QueryRun;
import ninja.NinjaDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

public class QueryRunDaoQueryTest extends NinjaDaoTestBase {
    protected QueryRunDao dao;
    protected QueryRun queryRun0;
    protected Integer queryRunId;

    @Before
    public void setUpQueryRunDaoQueryTest() {
        dao = getInstance(QueryRunDao.class);
        Datasource datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);

        queryRun0 = queryRun();
        queryRun0.setDatasource(datasource);
        dao.save(queryRun0);

        queryRunId = queryRun0.getId();

        QueryRun queryRun1 = queryRun();
        queryRun1.setDatasource(datasource);
        queryRun1.setLabel("unique test label");
        dao.save(queryRun1);
    }

    @Test
    public void testGetByLabel() {
        QueryRun fromDb = dao.getByLabel(queryRun0.getLabel());
        assertThat(fromDb, notNullValue());
    }

    @Test
    public void testGetAll() {
        List<QueryRun> queryRuns = dao.getAll();
        assertThat(queryRuns, hasSize(2));
        assertThat(queryRuns, hasItems(instanceOf(QueryRun.class)));
    }

    @Test
    public void testGetById() {
        assertThat(dao.getById(queryRunId), notNullValue());
        assertThat(dao.getById(100), nullValue());
    }
}
