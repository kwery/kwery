package controllers.apis.integration.datasourceapicontroller;

import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static controllers.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AddDatasourceSuccessTest extends DatasoureApiControllerTest {
    @Test
    public void test() throws IOException {
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(addDatasourceApi, datasource));
        String successMessage = MessageFormat.format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, datasource.getLabel());
        assertSuccess(successResult, successMessage);

        assertThat(datasourceDao.getByLabel(datasource.getLabel()), notNullValue());
    }
}
