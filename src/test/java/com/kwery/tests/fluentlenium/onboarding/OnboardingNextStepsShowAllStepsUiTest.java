package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.hook.wait.Wait;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.util.Messages.NEXT_STEP_ADD_DATASOURCE_M;
import static com.kwery.tests.util.Messages.NEXT_STEP_ADD_JOB_M;

public class OnboardingNextStepsShowAllStepsUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected OnboardingNextStepsPage page;

    @Before
    public void setUpOnboardingNextStepsShowAllPageTest() throws InterruptedException {
        page.go();
        page.isAt();;
    }

    @Test
    public void test() {
        page.nextStepsCount();
        page.nextStepsHeaderText();
        page.nextStepText(0, NEXT_STEP_ADD_DATASOURCE_M);
        page.nextStepText(1, NEXT_STEP_ADD_JOB_M);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
