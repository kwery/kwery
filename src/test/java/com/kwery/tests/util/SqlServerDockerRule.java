package com.kwery.tests.util;

import org.junit.rules.ExternalResource;

public class SqlServerDockerRule extends ExternalResource {
    protected SqlServerDocker sqlServerDocker;

    @Override
    public void before() {
        sqlServerDocker = new SqlServerDocker();
        sqlServerDocker.start();
    }

    @Override
    public void after() {
        sqlServerDocker.tearDown();
    }

    public SqlServerDocker getSqlServerDocker() {
        return sqlServerDocker;
    }
}
