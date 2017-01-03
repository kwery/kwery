package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.views.ActionResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.ADD_DATASOURCE_API;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static java.text.MessageFormat.format;

public class AddDatasourceFailureTest extends AbstractPostLoginApiTest {
    protected Datasource datasource;

    @Before
    public void addDatasourceFailureTestSetup() {
        datasource = datasource();
        datasourceDbSetup(datasource);
    }

    @Test
    public void test() throws IOException {
        String connectionFailureErrorMessage = "Failed to connect to MYSQL datasource. Communications link failure\n" +
                "\n" +
                "The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server. SQL State - 08S01.";

        datasource.setId(null);
        ActionResult failureResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_DATASOURCE_API), datasource));
        assertFailure(failureResult,
                format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                connectionFailureErrorMessage
        );
    }
}
