package dao.datasourcedao;

import dao.DatasourceDao;
import models.Datasource;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import java.util.List;

import static models.Datasource.Type.MYSQL;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;

public class DatasourceDaoQueryTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;

    protected Datasource savedDatasource0;
    protected Datasource savedDatasource1;

    @Before
    public void datasourceDaoQueryTestSetup() {
        datasourceDao = getInstance(DatasourceDao.class);
        savedDatasource0 = datasource();
        datasourceDao.save(savedDatasource0);

        savedDatasource1 = new Datasource();
        savedDatasource1.setUrl("goo.com");
        savedDatasource1.setPort(5432);
        savedDatasource1.setUsername("testuser");
        savedDatasource1.setPassword("password");
        savedDatasource1.setLabel("foo");
        savedDatasource1.setType(MYSQL);
        datasourceDao.save(savedDatasource1);
    }

    @Test
    public void testQueryByLabel() {
        assertThat(datasourceDao.getByLabel(datasource().getLabel()), notNullValue(Datasource.class));
        assertThat(datasourceDao.getByLabel(datasource().getLabel() + "foo"), nullValue(Datasource.class));
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
