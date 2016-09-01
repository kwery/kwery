package fluentlenium.user.admin;

import fluentlenium.RepoDashFluentLeniumTest;
import models.User;
import org.junit.Before;
import util.TestUtil;

public abstract class AddAdminUserTest extends RepoDashFluentLeniumTest {
    protected AddAdminUserPage page;
    protected User user;

    @Before
    public void setUpAddAdminUserTest() {
        page = createPage(AddAdminUserPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        page.isRendered();
        user = TestUtil.user();
    }
}
