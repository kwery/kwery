package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
public class UserLogoutUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Page
    protected UserLoginPage page;

    @Before
    public void setUp() {
        User user = TestUtil.user();
        user.setEmail("foo@goo.com");
        userDbSetUp(user);
        page.go();
        if (!page.isRendered()) {
            throw new RuntimeException("Could not render login page");
        }
        page.submitForm(user.getEmail(), user.getPassword());
        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        el("a.f-username").withHook(WaitHook.class).click();
        el(className("f-logout")).withHook(WaitHook.class).click();
        assertThat(el("div", withClass().contains("login-f"))).isDisplayed();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
