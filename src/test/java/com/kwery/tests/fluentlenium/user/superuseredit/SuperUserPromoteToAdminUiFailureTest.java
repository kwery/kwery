package com.kwery.tests.fluentlenium.user.superuseredit;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.USER_EDIT_SUPERUSER_FAILURE_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.user;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SuperUserPromoteToAdminUiFailureTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected LoginRule loginRule = new LoginRule(ninjaServerRule, this, true);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(loginRule);

    @Page
    protected SuperUserEditPage page;
    private UserDao userDao;
    private User user;

    @Before
    public void setUp() {
        user = user();
        user.setSuperUser(true);

        userDbSetUp(user);

        page.go(user.getId());

        userDao = ninjaServerRule.getInjector().getInstance(UserDao.class);

        page.waitForModalDisappearance();

        loginRule.getLoggedInUser().setSuperUser(false);
        userDao.update(loginRule.getLoggedInUser());
    }

    @Test
    public void test() {
        page.saveSuperUser(false);
        page.getActionResultComponent().assertFailureMessage(USER_EDIT_SUPERUSER_FAILURE_MESSAGE_M);
        assertThat(userDao.getById(user.getId()).getSuperUser(), is(true));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
