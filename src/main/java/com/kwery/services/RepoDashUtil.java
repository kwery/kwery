package com.kwery.services;

import com.google.inject.Singleton;
import com.kwery.models.Datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class RepoDashUtil {
    public Connection connection(Datasource datasource) throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger",  datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword());
    }
}
