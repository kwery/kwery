package com.kwery.tests.util;

import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.kwery.models.Datasource.Type.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;

public abstract class AbstractSqlDocker {
    public static final int DEFAULT_MYSQL_PORT = 3306;
    public static final int DEFAULT_POSTGRESQL_PORT = 5432;
    public static final int DEFAULT_SQLSERVER_PORT = 1433;

    protected CloudHost cloudHost;

    protected int port;
    protected int mappedPort;

    protected String host;

    protected DatasourceService datasourceService = new DatasourceService();

    public void start() {
        cloudHost = CloudHostFactory.getCloudHost(getType().name().toLowerCase());
        cloudHost.setup();

        this.mappedPort = cloudHost.getPort(this.port);
        this.host = cloudHost.getHostName();

        waitAtMost(2, MINUTES).until(() -> {
            try (Connection connection = DriverManager.getConnection(String.format(datasourceService.connectionString(datasource())), getUsername(), getPassword())) {
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
        datasource.setUsername(getUsername());
        datasource.setPassword(getPassword());

        String label = MYSQL.name();
        if (getType() == POSTGRESQL) {
            label = POSTGRESQL.name();
        }

        if (getType() == REDSHIFT) {
            label = REDSHIFT.name();
        }

        if (getType() == SQLSERVER) {
            label = SQLSERVER.name();
        }

        datasource.setLabel(label);

        datasource.setType(getType());

        if (getType() == POSTGRESQL || getType() == REDSHIFT) {
            datasource.setDatabase("postgres");
        } else if (getType() == SQLSERVER) {
            datasource.setDatabase("tempdb");
        }

        return datasource;
    }

    public abstract Datasource.Type getType();
    public abstract String getUsername();
    public abstract String getPassword();
}
