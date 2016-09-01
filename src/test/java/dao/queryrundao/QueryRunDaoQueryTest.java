package dao.queryrundao;

import dao.DatasourceDao;
import dao.QueryRunDao;
import models.Datasource;
import models.QueryRun;
import ninja.NinjaDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

public class QueryRunDaoQueryTest extends NinjaDaoTestBase {
    protected QueryRunDao dao;
    protected QueryRun queryRun;
    protected Datasource datasource;

    @Before
    public void setUpQueryRunDaoQueryTest() {
        dao = getInstance(QueryRunDao.class);
        Datasource datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);
        queryRun = queryRun();
        queryRun.setDatasource(datasource);
        dao.save(queryRun);
    }

    @Test
    public void test() {
        QueryRun fromDb = dao.getByLabel(queryRun.getLabel());
        assertThat(fromDb, notNullValue());
    }
}
