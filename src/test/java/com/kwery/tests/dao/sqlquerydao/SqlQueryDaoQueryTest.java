package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.queryRun;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
    public void testGetById() {
        assertThat(dao.getById(queryRunId), notNullValue());
        //The id is a random number which is chosen under the assumption that that particular id will not be present in the db
        assertThat(dao.getById(Integer.MAX_VALUE), nullValue());
    }
}
