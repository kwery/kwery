package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserUpdateUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();
    protected LoginRule loginRule = new LoginRule(ninjaServerRule, this);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(loginRule);

    @Page
    protected UserUpdatePage page;

    @Before
    public void setUpUpdateUserPageTest() {
        page.go(loginRule.getLoggedInUser().getId());

        if (!page.isRendered()) {
            failed("Could not render update user page");
        }
    }

    @Test
    public void test() {
        User user = loginRule.getLoggedInUser();
        page.waitForUsername(user.getUsername());
        assertThat(page.isUsernameDisabled(), is(true));
        page.updateForm("foo");
        page.waitForUserListPage();
        page.waitForSuccessMessage(user.getUsername());
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
