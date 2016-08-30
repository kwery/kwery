package controllers.fluentlenium.user.login;

import controllers.fluentlenium.RepoDashFluentLeniumTest;
import controllers.util.TestUtil;
import models.User;

public abstract class LoginTest extends RepoDashFluentLeniumTest {
    protected LoginPage page;
    protected User user = TestUtil.user();

    protected void initPage() {
        page = createPage(LoginPage.class);
        page.setBaseUrl(getServerAddress());
        goTo(page);
        page.isRendered();
    }
}
