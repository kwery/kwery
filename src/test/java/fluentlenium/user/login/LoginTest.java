package fluentlenium.user.login;

import fluentlenium.RepoDashFluentLeniumTest;
import models.User;
import org.junit.Before;

import static util.TestUtil.user;

public abstract class LoginTest extends RepoDashFluentLeniumTest {
    protected LoginPage page;
    protected User user;

    @Before
    public void setUpLoginTest() {
        page = createPage(LoginPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        page.isRendered();
        user = user();
    }
}
