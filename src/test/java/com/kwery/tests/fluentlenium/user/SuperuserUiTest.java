package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.tests.util.TestUtil.user;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@RunWith(Parameterized.class)
public class SuperuserUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Page
    protected UserLoginPage page;

    protected boolean superUser;

    @Parameters(name = "superUser{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true}, {false}
        });
    }

    private User user;

    public SuperuserUiTest(boolean superUser) {
        this.superUser = superUser;
    }

    @Before
    public void setUp() {
        user = user();
        user.setSuperUser(superUser);
        userDbSetUp(user);

        page.go();

        if (!page.isRendered()) {
            fail("Login page is not rendered");
        }

        page.submitForm(user.getEmail(), user.getPassword());
        page.waitForModalDisappearance();
    }

    @Test
    public void test() throws Exception {
        el(".settings-f").withHook(WaitHook.class).click();
        if (superUser) {
            assertThat(el("li", withClass().contains("user-setting-f"))).isDisplayed();
        } else {
            assertThat(el("li", withClass().contains("user-setting-f"))).isNotDisplayed();
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
