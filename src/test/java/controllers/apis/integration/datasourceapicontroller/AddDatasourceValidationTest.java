package controllers.apis.integration.datasourceapicontroller;

import models.Datasource;
import org.junit.Test;

import java.io.IOException;

import static controllers.util.Messages.LABEL_VALIDATION_M;
import static controllers.util.Messages.URL_VALIDATION_M;
import static controllers.util.Messages.USERNAME_VALIDATION_M;

public class AddDatasourceValidationTest extends DatasoureApiControllerTest {
    @Test
    public void test() throws IOException {
        Datasource invalid = new Datasource();
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(addDatasourceApi, invalid)),
                URL_VALIDATION_M, LABEL_VALIDATION_M, USERNAME_VALIDATION_M
        );
    }
}
