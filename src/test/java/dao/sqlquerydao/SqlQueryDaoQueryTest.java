package dao.sqlquerydao;

import dao.DatasourceDao;
import dao.SqlQueryDao;
import models.Datasource;
import models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

public class SqlQueryDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryDao dao;
    protected SqlQuery sqlQuery0;
    protected Integer queryRunId;

    @Before
    public void setUpQueryRunDaoQueryTest() {
        dao = getInstance(SqlQueryDao.class);
        Datasource datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);

        sqlQuery0 = queryRun();
        sqlQuery0.setDatasource(datasource);
        dao.save(sqlQuery0);

        queryRunId = sqlQuery0.getId();

        SqlQuery sqlQuery1 = queryRun();
        sqlQuery1.setDatasource(datasource);
        sqlQuery1.setLabel("unique test label");
        dao.save(sqlQuery1);
    }

    @Test
    public void testGetByLabel() {
        SqlQuery fromDb = dao.getByLabel(sqlQuery0.getLabel());
        assertThat(fromDb, notNullValue());
    }

    @Test
    public void testGetAll() {
        List<SqlQuery> sqlQueries = dao.getAll();
        assertThat(sqlQueries, hasSize(2));
        assertThat(sqlQueries, hasItems(instanceOf(SqlQuery.class)));
    }

    @Test
    public void testGetById() {
        assertThat(dao.getById(queryRunId), notNullValue());
        assertThat(dao.getById(100), nullValue());
    }
}
