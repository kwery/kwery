package com.kwery.tests.services;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.kwery.models.Datasource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.datasource.MysqlDatasourceService;
import com.kwery.tests.util.TestUtil;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static com.kwery.tests.util.TestUtil.datasource;

public class MysqlDatasourceServiceTest {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected MysqlDatasourceService mysqlDatasourceService;

    @Before
    public void before() throws IOException {
        datasource = datasource();

        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        String mysqlHost = cloudHost.getHostName();
        datasource.setUrl(mysqlHost);

        int port = cloudHost.getPort(datasource.getPort());
        datasource.setPort(port);

        mysqlDatasourceService = new MysqlDatasourceService();

        if (!TestUtil.waitForMysql(mysqlHost, port)) {
            fail("MySQL docker service is not up");
        }
    }

    @Test
    public void testSuccess() {
        assertThat(mysqlDatasourceService.testConnection(datasource), is(true));
    }

    @After
    public void after() {
        cloudHost.teardown();
    }
}
