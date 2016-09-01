package fluentlenium.datasource;

import dao.DatasourceDao;
import org.junit.Before;
import org.junit.Test;

import static java.text.MessageFormat.format;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;

public class AddDatasourceFailureTest extends DatasourceTest {
    @Before
    public void setUpAddDatasourceFailureTest() {
        getInjector().getInstance(DatasourceDao.class).save(datasource);
    }

    @Test
    public void test() {
        page.submitForm(datasource.getUrl() + "sjdfldsjf", String.valueOf(datasource.getPort()), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
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
