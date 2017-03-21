package com.kwery.tests.fluentlenium.user.superuseredit;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.user.superuseredit.SuperUserEditPage.FormField.confirmPassword;
import static com.kwery.tests.fluentlenium.user.superuseredit.SuperUserEditPage.FormField.password;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.user;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SuperUserEditPasswordResetUiTest extends ChromeFluentTest {
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
        userDbSetUp(user);

        page.go(user.getId());

        userDao = ninjaServerRule.getInjector().getInstance(UserDao.class);
    }

    @Test
    public void testSuccess() {
        String password = "foobarmoo";
        page.resetPassword(password, password);
        page.getActionResultComponent().assertSuccessMessage(Messages.USER_EDIT_PASSWORD_SUCCESS_MESSAGE_M);
        assertThat(userDao.getById(user.getId()).getPassword(), is(password));
    }

    @Test
    public void testEmptyPassword() {
        page.resetPassword("", "");
        page.assertNonEmptyValidationMessage(password);
        page.assertNonEmptyValidationMessage(confirmPassword);
    }

    @Test
    public void testPasswordMismatch() {
        page.resetPassword("foobarmoo", "foogooboo");
        page.assertNonEmptyValidationMessage(confirmPassword);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
