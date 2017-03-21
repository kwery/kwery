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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.USER_EDIT_SUPERUSER_SUCCESS_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.user;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class SuperUserPromoteToAdminUiSuccessTest extends ChromeFluentTest {
    protected boolean superUser;

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    public SuperUserPromoteToAdminUiSuccessTest(boolean superUser) {
        this.superUser = superUser;
    }

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
        user.setSuperUser(superUser);

        userDbSetUp(user);

        page.go(user.getId());

        userDao = ninjaServerRule.getInjector().getInstance(UserDao.class);

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        page.assertSuperUserState(superUser);
        page.saveSuperUser(!superUser);
        page.getActionResultComponent().assertSuccessMessage(USER_EDIT_SUPERUSER_SUCCESS_MESSAGE_M);
        assertThat(userDao.getById(user.getId()).getSuperUser(), is(!superUser));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
