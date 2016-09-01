package controllers.apis.integration.datasourceapicontroller.datasourceconnectiontest;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import models.Datasource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.TestUtil;
import views.ActionResult;

import java.io.IOException;

import static conf.Routes.MYSQL_DATASOURCE_CONNECTION_TEST_API;
import static org.junit.Assert.fail;
import static util.Messages.MYSQL_DATASOURCE_CONNECTION_SUCCESS_M;
import static util.TestUtil.datasource;

public class DatasourceConnectionTestSuccessTest extends AbstractPostLoginApiTest {
    protected CloudHost cloudHost;
    protected Datasource datasource;

    @Before
    public void setupDatasourceConnectionTestTest() {
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        datasource = datasource();

        String host = cloudHost.getHostName();
        datasource.setUrl(host);

        int port = cloudHost.getPort(datasource.getPort());
        datasource.setPort(port);

        if (!TestUtil.waitForMysql(host, port)) {
            fail("Failed to bring up MySQL docker service");
        }
    }

    @Test
    public void testSuccess() throws IOException {
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(getUrl(MYSQL_DATASOURCE_CONNECTION_TEST_API), datasource));
        assertSuccess(successResult, MYSQL_DATASOURCE_CONNECTION_SUCCESS_M);
    }
    @After
    public void datsourceConnectionTestTestTearDown() {
        cloudHost.teardown();
    }
}
