package com.kwery.tests.util;

import com.kwery.models.Datasource;

public class PostgreSqlDocker extends AbstractSqlDocker {
    protected Datasource.Type type = Datasource.Type.POSTGRESQL;
    protected String username = "postgres";
    protected String password = "root";

    public PostgreSqlDocker() {
        this.port = AbstractSqlDocker.DEFAULT_POSTGRESQL_PORT;
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
