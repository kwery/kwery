package dao;

import models.Datasource;
import org.junit.Before;
import org.junit.Test;

import static controllers.util.TestUtil.datasource;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class DatasourceDaoQueryTest extends BaseDatasourceDaoPersistTest {
    @Before
    public void datasourceDaoQueryTestSetup() {
        dao.save(datasource());
    }

    @Test
    public void testQueryByLabel() {
        assertThat(dao.getByLabel(datasource().getLabel()), notNullValue(Datasource.class));
        assertThat(dao.getByLabel(datasource().getLabel() + "foo"), nullValue(Datasource.class));
    }
}
