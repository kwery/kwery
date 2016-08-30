package controllers.fluentlenium.datasource;

import dao.DatasourceDao;
import org.junit.Before;
import org.junit.Test;

import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static controllers.util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;
import static java.text.MessageFormat.format;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class AddDatasourceFailureTest extends DatasourceTest {
    @Before
    public void before() {
        getInjector().getInstance(DatasourceDao.class).save(datasource);
    }

    @Test
    public void test() {
        initPage();
        page.submitForm(datasource.getUrl() + "sjdfldsjf", datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        page.waitForFailureMessage();
        assertThat(
                page.errorMessages(),
                containsInAnyOrder(
                        format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                        MYSQL_DATASOURCE_CONNECTION_FAILURE_M
                )
        );
    }
}
