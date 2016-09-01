package fluentlenium.user.login;

import fluentlenium.RepoDashFluentLeniumTest;
import models.User;
import util.TestUtil;

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
