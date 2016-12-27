package com.kwery.tests.fluentlenium.job;

import com.kwery.models.Datasource;
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
import static org.junit.rules.RuleChain.outerRule;

public class ReportSaveToggleCronExpressionDependsOnReportUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected ReportSavePage page;

    @Before
    public void setUpReportSaveSuccessUiTest() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        page = createPage(ReportSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }
    }

    @Test
    public void testDefaultExecuteAfterDisabled() {
        assertThat(page.isParentReportEnabled(), is(false));
        assertThat(page.isCronExpressionEnabled(), is(true));

        page.toggleParentReport();
        page.waitUntilParentReportIsEnabled();

        assertThat(page.isParentReportEnabled(), is(true));
        assertThat(page.isCronExpressionEnabled(), is(false));
    }
}
