package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.*;

public class OnboardingShowHomeScreenUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Before
    public void setUpOnboardingShowExecutingSqlQueriesPageTest() throws InterruptedException {
        datasourceDbSetup(datasource());
        jobDbSetUp(jobModelWithoutDependents());
        goTo(ninjaServerRule.getServerUrl() + "/");
    }

    //TODO -  Replace with ReportListPage
    @Test
    public void test() {
        await().atMost(TIMEOUT_SECONDS).until($(".report-list-table-f")).displayed();
    }
}
