package com.kwery.tests.fluentlenium.user.save.signup;

import com.kwery.tests.fluentlenium.user.save.SaveUtil;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.util.Messages.SIGN_UP_SUCCESS_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.tests.util.TestUtil.user;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UserSignUpSuccessUiTest extends AbstractUserSignUpSetUpUiTest {
    protected boolean onboardingFlow;

    public UserSignUpSuccessUiTest(boolean onboardingFlow) {
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
        super.setOnboardingFlow(onboardingFlow);
        super.setUp();
    }

    @Test
    public void test() throws Exception {
        page.saveUser(SaveUtil.toForm(user()));
        Awaitility.await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(ninjaServerRule.getServerUrl() + "/#user/login"));
        page.getActionResultComponent().assertSuccessMessage(SIGN_UP_SUCCESS_MESSAGE_M);
        assertThat(userDao.list(), hasSize(1));
    }
}
