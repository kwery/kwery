package com.kwery.tests.fluentlenium.user.login;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;

public class UserLoginFailureUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected UserLoginPage page;

    @Test
    public void test() {
        page = createPage(UserLoginPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        if (!page.isRendered()) {
            TestCase.fail("Could not render user login page");
        }

        page.submitForm("purvi", "bestDaughter");
        page.waitForFailureMessage();
    }
}
