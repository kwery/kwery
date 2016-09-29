package services.scheduler;

import models.Datasource;
import models.QueryRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlQueryRunner implements QueryRunner {
    protected static Logger logger = LoggerFactory.getLogger(MysqlQueryRunner.class);

    @Override
    public void run(Datasource datasource, QueryRun queryRun) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d", datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword())) {
            PreparedStatement p = connection.prepareCall(queryRun.getQuery());
            p.executeQuery();
            logger.info(String.format("Successfully executed query %s against datasource %s", queryRun.getQuery(), datasource.getLabel()));
            p.close();
            logger.info(String.format("Successfully connected to MySQL datasource %s with label %s", datasource.getUrl(), datasource.getLabel()));
        }
    }
}
