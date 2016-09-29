package fluentlenium.queryrun;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import dao.DatasourceDao;
import dao.UserDao;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import models.Datasource;
import models.User;
import org.junit.After;
import org.junit.Before;
import util.TestUtil;

import static org.junit.Assert.fail;
import static util.TestUtil.datasource;
import static util.TestUtil.user;

public class QueryRunTest extends RepoDashFluentLeniumTest {
    protected AddQueryRunPage page;
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

        page = createPage(AddQueryRunPage.class);
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
