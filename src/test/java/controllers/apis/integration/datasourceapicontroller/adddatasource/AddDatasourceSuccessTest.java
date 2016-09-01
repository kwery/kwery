package controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import models.Datasource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static conf.Routes.ADD_DATASOURCE_API;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static util.TestUtil.datasource;
import static util.TestUtil.waitForMysql;

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
