package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.dao.JobLabelDao;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static junit.framework.TestCase.fail;
import static org.junit.rules.RuleChain.outerRule;

public abstract class AbstractReportLabelUpdateUiTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    ReportLabelUpdatePage page;
    JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        page.setReportLabel(getReportLabel());
        page.go(getReportLabelId());

        if (!page.isRendered()) {
            fail("Could not render report label update page");
        }

        jobLabelDao = ninjaServerRule.getInjector().getInstance(JobLabelDao.class);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    public abstract int getReportLabelId();
    public abstract String getReportLabel();
}
