package fluentlenium.datasource;

import dao.UserDao;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import models.Datasource;
import models.User;
import org.junit.Before;

import static org.junit.Assert.fail;
import static util.TestUtil.datasource;
import static util.TestUtil.user;

public class DatasourceTest extends RepoDashFluentLeniumTest {
    protected AddDatasourcePage page;
    protected Datasource datasource;

    @Before
    public void setUpDatasourceTest() {
        datasource = datasource();

        User user = user();
        getInjector().getInstance(UserDao.class).save(user);

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Login page is not rendered");
        }
        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user);

        page = createPage(AddDatasourcePage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Add datasource page is not rendered");
        }
    }
}
