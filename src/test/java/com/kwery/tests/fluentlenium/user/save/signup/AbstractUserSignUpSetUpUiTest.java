package com.kwery.tests.fluentlenium.user.save.signup;

import com.kwery.dao.UserDao;
import com.kwery.tests.fluentlenium.user.save.UserSavePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class AbstractUserSignUpSetUpUiTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Page
    protected UserSavePage page;
    protected UserDao userDao;

    @Before
    public void setUp() {
        page.go();
        userDao = ninjaServerRule.getInjector().getInstance(UserDao.class);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    protected void assertEmptyUsersTable() {
        assertThat(ninjaServerRule.getInjector().getInstance(UserDao.class).list(), hasSize(0));
    }
}
