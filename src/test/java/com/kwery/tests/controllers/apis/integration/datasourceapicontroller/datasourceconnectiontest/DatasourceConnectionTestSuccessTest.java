package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.datasourceconnectiontest;

import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.PostgreSqlDockerRule;
import com.kwery.views.ActionResult;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.DATASOURCE_CONNECTION_TEST_API;
import static com.kwery.tests.util.Messages.DATASOURCE_CONNECTION_SUCCESS_M;
import static java.text.MessageFormat.format;

public class DatasourceConnectionTestSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Rule
    public PostgreSqlDockerRule postgreSqlDockerRule = new PostgreSqlDockerRule();

    @Test
    public void testMySqlSuccess() throws IOException {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(getUrl(DATASOURCE_CONNECTION_TEST_API), datasource));
        assertSuccess(successResult, format(DATASOURCE_CONNECTION_SUCCESS_M, datasource.getType()));
    }

    @Test
    public void testPostgreSqlSuccess() throws IOException {
        Datasource datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(getUrl(DATASOURCE_CONNECTION_TEST_API), datasource));
        assertSuccess(successResult, format(DATASOURCE_CONNECTION_SUCCESS_M, datasource.getType()));
    }
}
