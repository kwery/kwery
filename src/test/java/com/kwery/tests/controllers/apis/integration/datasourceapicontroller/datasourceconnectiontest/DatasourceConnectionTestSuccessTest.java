package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.datasourceconnectiontest;

import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.views.ActionResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.MYSQL_DATASOURCE_CONNECTION_TEST_API;
import static com.kwery.tests.util.Messages.MYSQL_DATASOURCE_CONNECTION_SUCCESS_M;

public class DatasourceConnectionTestSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;

    @Before
    public void setupDatasourceConnectionTestTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
    }

    @Test
    public void testSuccess() throws IOException {
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(getUrl(MYSQL_DATASOURCE_CONNECTION_TEST_API), datasource));
        assertSuccess(successResult, MYSQL_DATASOURCE_CONNECTION_SUCCESS_M);
    }
}
