package com.kwery.tests.fluentlenium.datasource;

import com.kwery.dao.UserDao;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.models.Datasource;
import com.kwery.models.User;
import org.junit.Before;

import static org.junit.Assert.fail;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.user;

public abstract class DatasourceAbstractTest extends RepoDashFluentLeniumTest {
    protected DatasourceAddPage page;
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

        page = createPage(DatasourceAddPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Add datasource page is not rendered");
        }
    }
}
