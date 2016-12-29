package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.datasourceconnectiontest;

import com.kwery.conf.Routes;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import org.junit.Test;
import com.kwery.tests.util.TestUtil;

import java.io.IOException;

import static com.kwery.tests.util.Messages.DATASOURCE_CONNECTION_FAILURE_M;

public class DatasourceConnectionTestFailureTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(
                        ninjaTestBrowser.postJson(getUrl(Routes.MYSQL_DATASOURCE_CONNECTION_TEST_API),
                                TestUtil.datasource())
                ),
                DATASOURCE_CONNECTION_FAILURE_M
        );
    }
}
