package com.kwery.tests.fluentlenium.user;

import com.google.common.collect.Lists;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Comparator;
import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;

public class UserListUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();
    protected LoginRule loginRule = new LoginRule(ninjaServerRule, this, true);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(loginRule);

    @Page
    protected UserListPage page;

    protected User user1;
    protected List<User> users;

    @Before
    public void before() {
        user1 = TestUtil.user();
        userDbSetUp(user1);

        users = Lists.newArrayList(loginRule.getLoggedInUser(), user1);
        users.sort(Comparator.comparing(User::getId));

        page.go();
        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        for (int i = 0; i < users.size(); ++i) {
            page.assertUserList(i, page.map(users.get(i)));
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
