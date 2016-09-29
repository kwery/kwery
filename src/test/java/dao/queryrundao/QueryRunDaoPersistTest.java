package dao.queryrundao;

import dao.DatasourceDao;
import dao.QueryRunDao;
import models.Datasource;
import models.QueryRun;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

public class QueryRunDaoPersistTest extends RepoDashDaoTestBase {
    protected QueryRunDao queryRunDao;
    private Datasource persistedDatasource;

    @Before
    public void setUpQueryRunDaoPersisTest() {
        queryRunDao = getInstance(QueryRunDao.class);
        persistedDatasource = datasource();
        getInstance(DatasourceDao.class).save(persistedDatasource);
    }

    @Test
    public void testPersist() {
        QueryRun q = queryRun();
        q.setDatasource(persistedDatasource);
        queryRunDao.save(q);
        assertThat(q.getId(), is(notNullValue()));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNullValuesValidation() {
        QueryRun q = new QueryRun();
        queryRunDao.save(q);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyValuesValidation() {
        QueryRun q = new QueryRun();
        q.setCronExpression("");
        q.setQuery("");
        q.setLabel("");
        queryRunDao.save(q);
    }
}
