package com.kwery.tests.services;

import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;

public class DatasourceServiceTest {
    @Rule
    public MySQLContainer mysql = new MySQLContainer();

    @Rule
    public PostgreSQLContainer postgres = new PostgreSQLContainer();

    protected DatasourceService datasourceService;

    @Before
    public void before() {
        datasourceService = new DatasourceService();
    }

    @Test
    public void testMysqlSuccess() throws SQLException {
        datasourceService.connect(TestUtil.datasource(mysql, Datasource.Type.MYSQL));
    }

    @Test
    public void testPostgreSqlSuccess() throws SQLException {
        datasourceService.connect(TestUtil.datasource(postgres, Datasource.Type.POSTGRESQL));
    }
}
