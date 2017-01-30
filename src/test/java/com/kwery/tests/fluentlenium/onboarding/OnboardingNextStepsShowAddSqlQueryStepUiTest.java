package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.datasource;

public class OnboardingNextStepsShowAddSqlQueryStepUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected OnboardingNextStepsPage page;

    @Before
    public void setUpOnboardingNextStepsShowAddSqlQueryStepUiTest() {
        datasourceDbSetup(datasource());
        page.go();
        page.isAt();
    }

    @Test
    public void test() {
        page.waitUntilAddJobStepDisplayed();
        page.isAddDatasourceNextStepDisplayed(false);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
