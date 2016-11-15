package services.datasource;

import models.Datasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlDatasourceService {
    private static Logger logger = LoggerFactory.getLogger(MysqlDatasourceService.class);

    public boolean testConnection(Datasource datasource) {
        try (Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger", datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword())) {
            logger.info(String.format("Successfully connected to MySQL datasource %s with label %s", datasource.getUrl(), datasource.getLabel()));
            return true;
        } catch (SQLException e) {
            logger.error("Exception while connecting to MySQL datasource  - " + datasource.getUrl(), e);
            return false;
        }
    }
}
