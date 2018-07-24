package com.kwery.tests.fluentlenium.user.login;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class UserLoginSuccessUiTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Test
    public void test() {
        //Login rule covers the test case
    }
}
