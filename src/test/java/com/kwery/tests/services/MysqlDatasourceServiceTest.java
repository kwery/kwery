package com.kwery.tests.services;

import com.kwery.models.Datasource;
import com.kwery.services.datasource.MysqlDatasourceService;
import com.kwery.tests.util.MysqlDockerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MysqlDatasourceServiceTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;
    protected MysqlDatasourceService mysqlDatasourceService;

    @Before
    public void before() throws IOException {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        mysqlDatasourceService = new MysqlDatasourceService();
    }

    @Test
    public void testSuccess() {
        assertThat(mysqlDatasourceService.testConnection(datasource), is(true));
    }
}
