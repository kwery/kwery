package fluentlenium.user.admin;

import fluentlenium.RepoDashFluentLeniumTest;
import models.User;
import util.TestUtil;

public abstract class AddAdminUserTest extends RepoDashFluentLeniumTest {
    protected AddAdminUserPage page;
    protected User user = TestUtil.user();

    protected void initPage() {
        page = createPage(AddAdminUserPage.class);
        page.setBaseUrl(getServerAddress());
        goTo(page);
        page.isRendered();
    }
}
