package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.awaitility.Awaitility;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.util.Messages.ONBOARDING_DATASOURCE_ADD_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(Parameterized.class)
public class OnboardingUserLoginUiTest extends ChromeFluentTest {
    protected boolean datasourcePresent;

    public OnboardingUserLoginUiTest(boolean datasourcePresent) {
        this.datasourcePresent = datasourcePresent;
    }

    static {
        System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
    }

    @Parameters(name = "DatasourcePresent{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Page
    protected UserLoginPage page;

    protected ActionResultComponent actionResultComponent;

    private User user;

    @Before
    public void setUp() {
        user = TestUtil.user();
        DbUtil.userDbSetUp(user);

        if (datasourcePresent) {
            DbUtil.datasourceDbSetup(TestUtil.datasource());
        }

        page.go();
    }

    @Test
    public void test() {
        page.submitForm(user.getEmail(), user.getPassword());

        if (datasourcePresent) {
            Awaitility.await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(ninjaServerRule.getServerUrl() + "/#report/add?onboarding=true"));
            actionResultComponent.assertInfoMessage(Messages.ONBOARDING_REPORT_ADD_M);
        } else {
            Awaitility.await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(ninjaServerRule.getServerUrl() + "/#datasource/add?onboarding=true"));
            actionResultComponent.assertInfoMessage(ONBOARDING_DATASOURCE_ADD_M);
        }

    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
