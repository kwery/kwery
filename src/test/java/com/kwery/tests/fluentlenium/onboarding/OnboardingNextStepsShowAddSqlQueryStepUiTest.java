package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.datasource;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingNextStepsShowAddSqlQueryStepUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected OnboardingNextStepsPage page;

    @Before
    public void setUpOnboardingNextStepsShowAddSqlQueryStepUiTest() {
        datasourceDbSetup(datasource());

        page = createPage(OnboardingNextStepsPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        if (!page.isRendered()) {
            fail("Could not render next steps page");
        }
    }

    @Test
    public void test() {
        assertThat(page.isAddDatasourceNextStepVisible(), is(false));
        assertThat(page.isAddSqlQueryNextStepVisible(), is(true));
    }
}
