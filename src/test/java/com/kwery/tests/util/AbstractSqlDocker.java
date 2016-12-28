package com.kwery.tests.util;

import com.kwery.models.Datasource;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.Datasource.Type.POSTGRESQL;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;

public abstract class AbstractSqlDocker {
    public static final int DEFAULT_MYSQL_PORT = 3306;
    public static final int DEFAULT_POSTGRESQL_PORT = 5432;

    protected CloudHost cloudHost;

    protected int port;
    protected int mappedPort;

    protected String host;

    public void start() {
        cloudHost = CloudHostFactory.getCloudHost(getType().name().toLowerCase());
        cloudHost.setup();

        this.mappedPort = cloudHost.getPort(this.port);
        this.host = cloudHost.getHostName();

        waitAtMost(2, MINUTES).until(() -> {
            try (Connection connection = DriverManager.getConnection(String.format(connectionString(getType()), host, mappedPort), getUsername(), getPassword())) {
                return true;
            } catch (SQLException e) {
                return false;
            }
        });
    }

    protected String connectionString(Datasource.Type type) {
        if (type == MYSQL) {
            return  "jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger";
        } else if (type == POSTGRESQL) {
            return  "jdbc:postgresql://%s:%d/?";
        }

        throw new AssertionError("Connection string not found for datasource type " + type);
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
        datasource.setUsername(getUsername());
        datasource.setPassword(getPassword());
        datasource.setLabel("label");
        datasource.setType(getType());
        return datasource;
    }

    public abstract Datasource.Type getType();
    public abstract String getUsername();
    public abstract String getPassword();
}
