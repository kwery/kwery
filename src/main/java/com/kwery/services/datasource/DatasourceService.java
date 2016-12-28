package com.kwery.services.datasource;

import com.kwery.models.Datasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatasourceService {
    private static Logger logger = LoggerFactory.getLogger(DatasourceService.class);

    public boolean testConnection(Datasource datasource) {
        try (Connection connection = DriverManager.getConnection(String.format(connectionString(datasource.getType()), datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword())) {
            logger.info(String.format("Successfully connected to %s datasource %s with label %s", datasource.getType(), datasource.getUrl(), datasource.getLabel()));
            return true;
        } catch (SQLException e) {
            logger.error("Exception while connecting to {} datasource  {}", datasource.getType(), datasource.getUrl(), e);
            return false;
        }
    }

    protected String connectionString(Datasource.Type type) {
        if (type == Datasource.Type.MYSQL) {
            return "jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger";
        } else if (type == Datasource.Type.POSTGRESQL) {
            return "jdbc:postgresql://%s:%d/?";
        }

        throw new AssertionError("JDBC connection string not present for type " + type);
    }

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        datasource.setId(1);
        datasource.setLabel("postgres");
        datasource.setPort(5432);
        datasource.setType(Datasource.Type.POSTGRESQL);
        datasource.setUrl("localhost");
        datasource.setUsername("postgres");
        datasource.setPassword("root");

        DatasourceService datasourceService = new DatasourceService();
        System.out.println(datasourceService.testConnection(datasource));
    }
}
