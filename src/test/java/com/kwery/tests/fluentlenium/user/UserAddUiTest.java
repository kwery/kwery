package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

public abstract class UserAddUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected LoginRule loginRule = new LoginRule(ninjaServerRule, this);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(loginRule);

    protected UserAddPage page;

    protected User loggedInUser;

    @Before
    public void setUpAddAdminUserTest() {
        page = createPage(UserAddPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        page.isRendered();

        loggedInUser = loginRule.getLoggedInUser();
    }
}
