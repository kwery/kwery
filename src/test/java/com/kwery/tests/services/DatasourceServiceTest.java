package com.kwery.tests.services;

import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.PostgreSqlDockerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

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
    public void testMysqlSuccess() throws SQLException {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasourceService.connect(datasource);
    }

    @Test
    public void testPostgreSqlSuccess() throws SQLException {
        Datasource datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();
        datasourceService.connect(datasource);
    }
}
