package controllers.fluentlenium.datasource;

import controllers.fluentlenium.RepoDashFluentLeniumTest;
import controllers.fluentlenium.user.login.LoginPage;
import controllers.util.TestUtil;
import dao.UserDao;
import models.Datasource;
import models.User;

import static org.junit.Assert.fail;

public class DatasourceTest extends RepoDashFluentLeniumTest {
    protected AddDatasourcePage page;

    protected Datasource datasource = TestUtil.datasource();

    public void initPage() {
        User user = TestUtil.user();
        getInjector().getInstance(UserDao.class).save(user);

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.setBaseUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Login page is not rendered");
        }
        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user);

        page = createPage(AddDatasourcePage.class);
        page.setBaseUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Add datasource page is not rendered");
        }
    }
}
