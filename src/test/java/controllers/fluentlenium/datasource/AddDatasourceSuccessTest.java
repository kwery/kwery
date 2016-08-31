package controllers.fluentlenium.datasource;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import controllers.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class AddDatasourceSuccessTest extends DatasourceTest {
    protected CloudHost cloudHost;

    @Override
    @Before
    public void startServer() {
        super.startServer();
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        String host = cloudHost.getHostName();
        int port = cloudHost.getPort(datasource.getPort());

        datasource.setUrl(host);
        datasource.setPort(port);

        if (!TestUtil.waitForMysql(host, port)) {
            fail("Could not bring up MySQL docker service");
        }
    }

    @Test
    public void test() {
        initPage();
        page.submitForm(datasource.getUrl(), String.valueOf(datasource.getPort()), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        page.waitForSuccessMessage(datasource.getLabel());
    }

    @After
    public void shutdownServer() {
        super.shutdownServer();
        cloudHost.teardown();
    }
}
