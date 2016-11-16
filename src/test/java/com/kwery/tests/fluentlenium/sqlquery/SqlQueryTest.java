package com.kwery.tests.fluentlenium.sqlquery;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.UserDao;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.models.Datasource;
import com.kwery.models.User;
import org.junit.After;
import org.junit.Before;
import com.kwery.tests.util.TestUtil;

import static org.junit.Assert.fail;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.user;

public class SqlQueryTest extends RepoDashFluentLeniumTest {
    protected AddSqlQueryPage page;
    protected CloudHost cloudHost;
    protected Datasource datasource;

    @Before
    public void setUpQueryRunTest() {
        User user = user();
        getInjector().getInstance(UserDao.class).save(user);

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress()).goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Login page is not rendered");
        }
        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user);

        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        datasource = datasource();

        String host = cloudHost.getHostName();
        int port = cloudHost.getPort(datasource.getPort());

        datasource.setUrl(host);
        datasource.setPort(port);

        if (!TestUtil.waitForMysql(host, port)) {
            fail("Could not bring up MySQL docker service");
        }

        getInjector().getInstance(DatasourceDao.class).save(datasource());

        page = createPage(AddSqlQueryPage.class);
        page.withDefaultUrl(getServerAddress()).goTo(page);
        if (!page.isRendered()) {
            fail("Add query run page is not rendered");
        }
    }

    @After
    public void tearDownQueryRunTest() {
        cloudHost.teardown();
    }
}
