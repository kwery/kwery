package controllers.fluentlenium.user.admin;

import controllers.fluentlenium.RepoDashFluentLeniumTest;
import controllers.util.TestUtil;
import models.User;

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
