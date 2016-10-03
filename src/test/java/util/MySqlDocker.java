package util;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import models.Datasource;

import static junit.framework.TestCase.fail;
import static models.Datasource.Type.MYSQL;

public class MySqlDocker {
    public static final int DEFAULT_PORT = 3306;
    protected CloudHost cloudHost;

    protected int port;
    protected int mappedPort;

    protected String host;

    public MySqlDocker() {
        this(DEFAULT_PORT);
    }

    public MySqlDocker(int port) {
        this.port = port;
    }

    public void start() {
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        this.mappedPort = cloudHost.getPort(this.port);
        this.host = cloudHost.getHostName();

        if (!TestUtil.waitForMysql(this.host, this.mappedPort)) {
            fail("MySQL docker service is not up");
        }
    }

    public String host() {
        return this.host;
    }

    public int mappedPort() {
        return this.mappedPort;
    }

    public void tearDown() {
        if (cloudHost != null) {
            cloudHost.teardown();
        }
    }

    public Datasource datasource() {
        Datasource datasource = new Datasource();
        datasource.setUrl(this.host);
        datasource.setPort(this.mappedPort);
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setLabel("label");
        datasource.setType(MYSQL);
        return datasource;
    }
}
