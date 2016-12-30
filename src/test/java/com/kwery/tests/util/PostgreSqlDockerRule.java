package com.kwery.tests.util;

import org.junit.rules.ExternalResource;

public class PostgreSqlDockerRule extends ExternalResource {
    protected PostgreSqlDocker postgreSqlDocker;

    @Override
    public void before() {
        postgreSqlDocker = new PostgreSqlDocker();
        postgreSqlDocker.start();
    }

    @Override
    public void after() {
        postgreSqlDocker.tearDown();
    }

    public PostgreSqlDocker getPostgreSqlDocker() {
        return postgreSqlDocker;
    }
}
