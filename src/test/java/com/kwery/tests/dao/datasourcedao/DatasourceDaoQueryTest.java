package com.kwery.tests.dao.datasourcedao;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.util.TestUtil.datasource;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class DatasourceDaoQueryTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;

    protected Datasource savedDatasource0;
    protected Datasource savedDatasource1;

    @Before
    public void datasourceDaoQueryTestSetup() {
        savedDatasource0 = datasource();
        DbUtil.datasourceDbSetup(savedDatasource0);

        savedDatasource1 = datasource();
        DbUtil.datasourceDbSetup(savedDatasource1);

        datasourceDao = getInstance(DatasourceDao.class);
    }

    @Test
    public void testQueryByLabel() {
        assertThat(datasourceDao.getByLabel(savedDatasource0.getLabel()), notNullValue(Datasource.class));
        assertThat(datasourceDao.getByLabel(savedDatasource0.getLabel() + "foo"), nullValue(Datasource.class));
    }

    @Test
    public void testQueryById() {
        assertThat(datasourceDao.getById(savedDatasource0.getId()), notNullValue());
    }

    @Test
    public void testGetAll() {
        List<Datasource> all = datasourceDao.getAll();
        assertThat(all, hasSize(2));
        assertThat(all, hasItems(instanceOf(Datasource.class)));
    }
}
