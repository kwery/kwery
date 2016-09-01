package controllers.apis.integration.datasourceapicontroller.datasourceconnectiontest;

import conf.Routes;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import org.junit.Test;
import util.TestUtil;

import java.io.IOException;

import static util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;

public class DatasourceConnectionTestFailureTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(
                        ninjaTestBrowser.postJson(getUrl(Routes.MYSQL_DATASOURCE_CONNECTION_TEST_API),
                                TestUtil.datasource())
                ),
                MYSQL_DATASOURCE_CONNECTION_FAILURE_M
        );
    }
}
