package com.kwery.tests.services;

import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.tests.util.MysqlDockerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatasourceServiceTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;
    protected DatasourceService datasourceService;

    @Before
    public void before() throws IOException {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasourceService = new DatasourceService();
    }

    @Test
    public void testSuccess() {
        assertThat(datasourceService.testConnection(datasource), is(true));
    }
}
