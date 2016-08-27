package controllers.apis.integration.datasourceapicontroller;

import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static models.Datasource.Type.MYSQL;

public class AddDatasourceFailureTest extends DatasoureApiControllerTest {
    @Before
    public void before() {
        super.before();
        datasourceDao.save(datasource);
    }

    @Test
    public void test() throws IOException {
        ActionResult failureResult = actionResult(ninjaTestBrowser.postJson(addDatasourceApi, datasource));
        String failureMessage = MessageFormat.format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel());
        assertFailure(failureResult, failureMessage);
    }
}
