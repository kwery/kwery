package controllers.fluentlenium.datasource;

import org.junit.Test;

public class AddDatasourceSuccessTest extends DatasourceTest {
    @Test
    public void test() {
        initPage();
        page.submitForm(datasource.getUrl(), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        page.waitForSuccessMessage(datasource.getLabel());
    }
}
