package controllers.apis.integration.datasourceapicontroller;

import models.Datasource;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;

import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static controllers.util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;
import static controllers.util.TestUtil.datasource;
import static java.text.MessageFormat.format;
import static models.Datasource.Type.MYSQL;

public class AddDatasourceFailureTest extends DatasoureApiControllerTest {
    @Before
    public void addDatasourceFailureTestSetup() {
        datasourceDao.save(datasource);
    }

    @Test
    public void test() throws IOException {
        Datasource datasource = datasource();
        datasource.setUrl("sldjfjkl");

        ActionResult failureResult = actionResult(ninjaTestBrowser.postJson(addDatasourceApi, datasource));
        assertFailure(failureResult,
                format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                MYSQL_DATASOURCE_CONNECTION_FAILURE_M
        );
    }
}
