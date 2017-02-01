package com.kwery.services.datasource;

import com.kwery.models.Datasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.kwery.models.Datasource.Type.*;

public class DatasourceService {
    private static Logger logger = LoggerFactory.getLogger(DatasourceService.class);

    public void connect(Datasource datasource) throws SQLException {
        try (Connection connection = DriverManager.getConnection(String.format(connectionString(datasource)), datasource.getUsername(), datasource.getPassword())) {
            logger.info(String.format("Successfully connected to %s datasource %s with label %s", datasource.getType(), datasource.getUrl(), datasource.getLabel()));
        }
    }

    public Connection connection(Datasource datasource) throws SQLException {
        return DriverManager.getConnection(String.format(connectionString(datasource), datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword());
    }

    public String connectionString(Datasource datasource) {
        if (datasource.getType() == MYSQL) {
            return String.format("jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger", datasource.getUrl(), datasource.getPort()) ;
        } else if ((datasource.getType() == POSTGRESQL) || (datasource.getType() == REDSHIFT)) {
            return String.format("jdbc:postgresql://%s:%d/%s", datasource.getUrl(), datasource.getPort(), datasource.getDatabase());
        }

        throw new AssertionError("JDBC connection string not present for type " + datasource.getType());
    }

    public static void main(String[] args) throws SQLException {
        Datasource datasource = new Datasource();
        datasource.setId(1);
        datasource.setLabel("postgres");
        datasource.setPort(5432);
        datasource.setType(POSTGRESQL);
        datasource.setUrl("localhost");
        datasource.setUsername("postgres");
        datasource.setPassword("root");

        DatasourceService datasourceService = new DatasourceService();
        datasourceService.connect(datasource);
    }
}
