package com.kwery.tests.fluentlenium.user;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static org.openqa.selenium.By.className;

public class UserLogoutUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Before
    public void setUpLogoutFlowTest() {
        goTo(ninjaServerRule.getServerUrl() + "/");
        await().atMost(TIMEOUT_SECONDS).until(".f-navbar").isDisplayed();
    }

    @Test
    public void test() {
        $(className("f-username")).click();
        await().atMost(TIMEOUT_SECONDS).until(".f-logout").isDisplayed();
        $(className("f-logout")).click();
        await().atMost(TIMEOUT_SECONDS).until(".f-next-steps").isDisplayed();
    }
}
