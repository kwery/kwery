package services.scheduler;

import models.Datasource;
import models.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlSqlQueryRunner implements SqlQueryRunner {
    protected static Logger logger = LoggerFactory.getLogger(MysqlSqlQueryRunner.class);

    @Override
    public void run(Datasource datasource, SqlQuery sqlQuery) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d", datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword())) {
            PreparedStatement p = connection.prepareCall(sqlQuery.getQuery());
            p.executeQuery();
            logger.info(String.format("Successfully executed query %s against datasource %s", sqlQuery.getQuery(), datasource.getLabel()));
            p.close();
            logger.info(String.format("Successfully connected to MySQL datasource %s with label %s", datasource.getUrl(), datasource.getLabel()));
        }
    }
}
