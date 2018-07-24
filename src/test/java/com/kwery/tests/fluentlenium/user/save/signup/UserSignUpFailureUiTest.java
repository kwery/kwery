package com.kwery.tests.fluentlenium.user.save.signup;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.save.SaveUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.SIGN_UP_FAILURE_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.user;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UserSignUpFailureUiTest extends AbstractUserSignUpSetUpUiTest {
    private User existingUser;

    protected boolean onboardingFlow;

    public UserSignUpFailureUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true},
                {false},
        });
    }

    @Before
    public void setUp() {
        existingUser = user();
        userDbSetUp(existingUser);
        super.setOnboardingFlow(onboardingFlow);
        super.setUp();
    }

    @Test
    public void test() throws Exception {
        User user = user();
        user.setEmail(existingUser.getEmail());
        page.saveUser(SaveUtil.toForm(user));
        page.waitForModalDisappearance();
        page.getActionResultComponent().assertFailureMessage(MessageFormat.format(SIGN_UP_FAILURE_MESSAGE_M, user.getEmail()));
        assertThat(userDao.list(), hasSize(1));
    }
}
