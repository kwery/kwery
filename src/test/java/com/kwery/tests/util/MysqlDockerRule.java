package com.kwery.tests.util;

import org.junit.rules.ExternalResource;

public class MysqlDockerRule extends ExternalResource {
    protected MySqlDocker mySqlDocker;

    @Override
    public void before() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();
    }

    @Override
    public void after() {
        mySqlDocker.tearDown();
    }

    public MySqlDocker getMySqlDocker() {
        return mySqlDocker;
    }
}
