package com.kwery.tests.fluentlenium.job;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportSaveAddAndRemoveSqlQuerySectionUiTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    ReportSavePage page;

    @Before
    public void setUpReportSaveAddAndRemoveSqlQuerySectionUiTest() {
        page = createPage(ReportSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }
    }

    @Test
    public void test() {
        page.clickOnAddSqlQuery(0);
        assertThat("All SQL query add sections have remove action", page.removeSqlQueryActionDisplayedCount(), is(2));
        page.clickOnRemoveSqlQuery(1);
        assertThat("All SQL query add sections have remove action", page.removeSqlQueryActionDisplayedCount(), is(0));
    }
}
