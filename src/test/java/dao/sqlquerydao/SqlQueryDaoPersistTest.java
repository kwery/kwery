package dao.sqlquerydao;

import dao.DatasourceDao;
import dao.SqlQueryDao;
import models.Datasource;
import models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

public class SqlQueryDaoPersistTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;
    private Datasource persistedDatasource;

    @Before
    public void setUpQueryRunDaoPersisTest() {
        sqlQueryDao = getInstance(SqlQueryDao.class);
        persistedDatasource = datasource();
        getInstance(DatasourceDao.class).save(persistedDatasource);
    }

    @Test
    public void testPersist() {
        SqlQuery q = queryRun();
        q.setDatasource(persistedDatasource);
        sqlQueryDao.save(q);
        assertThat(q.getId(), is(notNullValue()));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNullValuesValidation() {
        SqlQuery q = new SqlQuery();
        sqlQueryDao.save(q);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyValuesValidation() {
        SqlQuery q = new SqlQuery();
        q.setCronExpression("");
        q.setQuery("");
        q.setLabel("");
        sqlQueryDao.save(q);
    }
}
