package fluentlenium.security;

import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.RepoDashPage;
import fluentlenium.datasource.AddDatasourcePage;
import fluentlenium.index.IndexPage;
import fluentlenium.user.admin.AddAdminUserPage;
import fluentlenium.user.login.LoginPage;
import org.fluentlenium.core.FluentPage;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PageAccessAuthenticationRequiredTest extends RepoDashFluentLeniumTest {
    protected Map<Class<? extends FluentPage>, Boolean> pageClasses;

    @Before
    public void setUpPageAccessAuthenticationRequiredTest() {
        pageClasses = new HashMap<>();
        pageClasses.put(LoginPage.class, false);
        pageClasses.put(AddAdminUserPage.class, false);
        pageClasses.put(IndexPage.class, false);
        pageClasses.put(AddDatasourcePage.class, true);
    }

    @Test
    public void test() {
        for (Map.Entry<Class<? extends FluentPage>, Boolean> entry : pageClasses.entrySet()) {
            FluentPage page = createPage(entry.getKey());
            page.withDefaultUrl(getServerAddress());
            goTo(page);

            if (entry.getValue()) {
                await().atMost(TIMEOUT_SECONDS, SECONDS).until($("#loginForm")).isDisplayed();
            } else {
                assertThat(((RepoDashPage)page).isRendered(), is(true));
            }
        }
    }
}
