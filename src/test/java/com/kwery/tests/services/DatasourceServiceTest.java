package com.kwery.tests.services;

import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.PostgreSqlDockerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatasourceServiceTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Rule
    public PostgreSqlDockerRule postgreSqlDockerRule = new PostgreSqlDockerRule();


    protected DatasourceService datasourceService;

    @Before
    public void before() throws IOException {
        datasourceService = new DatasourceService();
    }

    @Test
    public void testMysqlSuccess() {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();
        assertThat(datasourceService.testConnection(datasource), is(true));
    }

    @Test
    public void testPostgreSqlSuccess() {
        Datasource datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();
        assertThat(datasourceService.testConnection(datasource), is(true));
    }
}
