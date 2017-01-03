package com.kwery.tests.util;

import com.kwery.models.Datasource;

public class MySqlDocker extends AbstractSqlDocker {
    protected Datasource.Type type = Datasource.Type.MYSQL;
    protected String username = "root";
    protected String password = "root";

    public MySqlDocker() {
        this.port = AbstractSqlDocker.DEFAULT_MYSQL_PORT;
    }

    @Override
    public Datasource.Type getType() {
        return type;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
