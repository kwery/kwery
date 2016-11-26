package com.kwery.tests.util;

import com.kwery.models.Datasource;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.kwery.models.Datasource.Type.MYSQL;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;

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

        waitAtMost(2, MINUTES).until(() -> {
            try (Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger", host, mappedPort), "root", "root")) {
                return true;
            } catch (SQLException e) {
                return false;
            }
        });
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
