package com.kwery.tests.fluentlenium.user.save.edit;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;

public class AbstractUserEditSetUpUiTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected UserEditPage page;

    protected User existingUser;

    protected UserDao userDao;

    @Before
    public void setUp() {
        existingUser = TestUtil.user();
        userDbSetUp(existingUser);
        page.go(existingUser.getId());
        userDao = ninjaServerRule.getInjector().getInstance(UserDao.class);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
