package com.kwery.tests.util;

import com.kwery.models.Datasource;

public class SqlServerDocker extends AbstractSqlDocker {
    protected Datasource.Type type = Datasource.Type.SQLSERVER;
    protected String username = "sa";
    protected String password = "Thisisalongpassword9#";

    public SqlServerDocker() {
        this.port = AbstractSqlDocker.DEFAULT_SQLSERVER_PORT;
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

    public static void main(String[] args) {
        SqlServerDocker sqlServerDocker = new SqlServerDocker();
        sqlServerDocker.start();
        sqlServerDocker.tearDown();
    }
}
