package com.kwery.tests.fluentlenium.joblabel;

import com.kwery.dao.JobLabelDao;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static junit.framework.TestCase.fail;
import static org.junit.rules.RuleChain.outerRule;

public class AbstractReportLabelSaveTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    ReportLabelSavePage page;
    JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        page = createPage(ReportLabelSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report label save page");
        }

        jobLabelDao = ninjaServerRule.getInjector().getInstance(JobLabelDao.class);
    }
}
