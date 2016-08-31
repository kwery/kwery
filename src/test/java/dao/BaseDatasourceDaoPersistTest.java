package dao;

import ninja.NinjaDaoTestBase;
import org.junit.Before;

public class BaseDatasourceDaoPersistTest extends NinjaDaoTestBase {
    protected DatasourceDao dao;

    @Before
    public void baseDatasourceDaoPersisTestSetup() {
        dao = getInstance(DatasourceDao.class);
    }
}
