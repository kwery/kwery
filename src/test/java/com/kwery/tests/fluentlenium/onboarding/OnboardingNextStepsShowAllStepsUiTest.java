package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.onboarding.OnboardingNextStepsPage.NEXT_STEPS_COUNT;
import static com.kwery.tests.util.Messages.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingNextStepsShowAllStepsUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected OnboardingNextStepsPage page;

    @Before
    public void setUpOnboardingNextStepsShowAllPageTest() throws InterruptedException {
        page = newInstance(OnboardingNextStepsPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render next steps page");
        }
    }

    @Test
    public void test() {
        assertThat(page.nextStepsCount(), is(NEXT_STEPS_COUNT));
        assertThat(page.nextStepsHeaderText(), is(NEXT_STEP_HEADER_M));
        assertThat(page.nextStepText(0), is(NEXT_STEP_ADD_DATASOURCE_M));
        assertThat(page.nextStepText(1), is(NEXT_STEP_ADD_JOB_M));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
