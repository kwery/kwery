package services;

import com.google.inject.Singleton;
import models.Datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class RepoDashUtil {
    public Connection connection(Datasource datasource) throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:mysql://%s:%d",  datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword());
    }
}
