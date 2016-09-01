package dao.datasourcedao;

import dao.DatasourceDao;
import models.Datasource;
import ninja.NinjaDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static util.TestUtil.datasource;

public class DatasourceDaoQueryTest extends NinjaDaoTestBase {
    protected DatasourceDao datasourceDao;

    @Before
    public void datasourceDaoQueryTestSetup() {
        datasourceDao = getInstance(DatasourceDao.class);
        datasourceDao.save(datasource());
    }

    @Test
    public void testQueryByLabel() {
        assertThat(datasourceDao.getByLabel(datasource().getLabel()), notNullValue(Datasource.class));
        assertThat(datasourceDao.getByLabel(datasource().getLabel() + "foo"), nullValue(Datasource.class));
    }
}
