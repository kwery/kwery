package dao;

import models.Datasource;
import ninja.NinjaDaoTestBase;
import org.junit.Before;

import static models.Datasource.Type.MYSQL;

public class BaseDatasourceDaoPersistTest extends NinjaDaoTestBase {
    protected DatasourceDao dao;

    @Before
    public void before() {
        dao = getInstance(DatasourceDao.class);
    }

    protected Datasource datasource() {
        Datasource d = new Datasource();
        d.setUsername("purvi");
        d.setPassword("password");
        d.setUrl("com.foo");
        d.setLabel("testDb");
        d.setType(MYSQL);
        return d;
    }
}
