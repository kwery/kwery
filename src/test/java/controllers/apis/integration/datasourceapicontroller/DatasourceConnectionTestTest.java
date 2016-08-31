package controllers.apis.integration.datasourceapicontroller;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import controllers.util.TestUtil;
import models.Datasource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;

import static controllers.util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;
import static controllers.util.Messages.MYSQL_DATASOURCE_CONNECTION_SUCCESS_M;
import static org.junit.Assert.fail;

public class DatasourceConnectionTestTest extends DatasoureApiControllerTest {
    protected CloudHost cloudHost;

    @Before
    public void setupDatasourceConnectionTestTest() {
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

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
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(mysqlDatasourceConnectionTestApi, datasource));
        assertSuccess(successResult, MYSQL_DATASOURCE_CONNECTION_SUCCESS_M);
    }

    @Test
    public void testFailure() throws IOException {
        Datasource datasource = TestUtil.datasource();
        datasource.setUrl("lskdjfkldsjf");
        ActionResult result = actionResult(ninjaTestBrowser.postJson(mysqlDatasourceConnectionTestApi, datasource));
        assertFailure(result, MYSQL_DATASOURCE_CONNECTION_FAILURE_M);
    }

    @After
    public void datsourceConnectionTestTestTearDown() {
        cloudHost.teardown();
    }
}
