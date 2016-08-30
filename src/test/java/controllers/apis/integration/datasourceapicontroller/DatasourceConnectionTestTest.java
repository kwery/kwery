package controllers.apis.integration.datasourceapicontroller;

import models.Datasource;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;

import static controllers.util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;
import static controllers.util.Messages.MYSQL_DATASOURCE_CONNECTION_SUCCESS_M;

public class DatasourceConnectionTestTest extends DatasoureApiControllerTest {
    @Test
    public void testSuccess() throws IOException {
        Datasource datasource = new Datasource();
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setUrl("localhost");
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(mysqlDatasourceConnectionTestApi, datasource));
        assertSuccess(successResult, MYSQL_DATASOURCE_CONNECTION_SUCCESS_M);
    }

    @Test
    public void testFailure() throws IOException {
        ActionResult result = actionResult(ninjaTestBrowser.postJson(mysqlDatasourceConnectionTestApi, datasource));
        assertFailure(result, MYSQL_DATASOURCE_CONNECTION_FAILURE_M);
    }
}
