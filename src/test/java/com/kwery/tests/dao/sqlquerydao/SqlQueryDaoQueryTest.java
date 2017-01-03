package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.exparity.hamcrest.BeanMatchers.theSameAs;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryDao dao;

    protected SqlQueryModel sqlQueryModel;

    @Before
    public void setUpQueryRunDaoQueryTest() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);

        dao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void testGetByLabel() {
        assertThat(dao.getByLabel(sqlQueryModel.getLabel()), theSameAs(sqlQueryModel));
        assertThat(dao.getByLabel(UUID.randomUUID().toString()), nullValue());
    }

    @Test
    public void testGetById() {
        assertThat(dao.getById(sqlQueryModel.getId()), theSameAs(sqlQueryModel));
        assertThat(dao.getById(Integer.MAX_VALUE), nullValue());
    }
}
