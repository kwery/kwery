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
import static com.kwery.tests.util.Messages.DATASOURCE_CONNECTION_FAILURE_M;
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
        datasource.setId(null);
        ActionResult failureResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_DATASOURCE_API), datasource));
        assertFailure(failureResult,
                format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                format(DATASOURCE_CONNECTION_FAILURE_M, datasource.getType().name())
        );
    }
}
