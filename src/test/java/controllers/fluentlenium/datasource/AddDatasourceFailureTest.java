package controllers.fluentlenium.datasource;

import dao.DatasourceDao;
import org.junit.Before;
import org.junit.Test;

public class AddDatasourceFailureTest extends DatasourceTest {
    @Before
    public void before() {
        getInjector().getInstance(DatasourceDao.class).save(datasource);
    }

    @Test
    public void test() {
        initPage();
        page.submitForm(datasource.getUrl(), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        page.waitForFailureMessage(datasource.getLabel());
    }
}
