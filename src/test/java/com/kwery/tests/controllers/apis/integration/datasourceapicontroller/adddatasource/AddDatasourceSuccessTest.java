package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static com.kwery.conf.Routes.ADD_DATASOURCE_API;
import static com.kwery.models.Datasource.Type.MYSQL;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.waitForMysql;

public class AddDatasourceSuccessTest extends AbstractPostLoginApiTest {
    protected CloudHost cloudHost;
    protected Datasource datasource;

    @Before
    public void addDatasourceSuccessTestSetup() {
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        datasource = datasource();

        String host = cloudHost.getHostName();
        datasource.setUrl(host);

        int port = cloudHost.getPort(datasource.getPort());
        datasource.setPort(port);

        if (!waitForMysql(host, port)) {
            fail("Could not bring up docker MySQL service");
        }
    }

    @Test
    public void test() throws IOException {
        ActionResult successResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_DATASOURCE_API), datasource));
        String successMessage = MessageFormat.format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, datasource.getLabel());
        assertSuccess(successResult, successMessage);

        assertThat(getInjector().getInstance(DatasourceDao.class).getByLabel(datasource.getLabel()), notNullValue());
    }

    @After
    public void addDatasourceSuccessTestTearDown() {
        cloudHost.teardown();
    }
}
