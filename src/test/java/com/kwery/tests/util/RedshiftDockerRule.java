package com.kwery.tests.util;

import org.junit.rules.ExternalResource;

public class RedshiftDockerRule extends ExternalResource {
    protected RedshiftDocker redshiftDocker;

    @Override
    public void before() {
        redshiftDocker = new RedshiftDocker();
        redshiftDocker.start();
    }

    @Override
    public void after() {
        redshiftDocker.tearDown();
    }

    public PostgreSqlDocker getRedshiftDocker() {
        return redshiftDocker;
    }
}
