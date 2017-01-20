package com.kwery.tests.fluentlenium.job;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.JobExecutionModel.Status.ONGOING;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static junit.framework.TestCase.fail;
import static org.junit.rules.RuleChain.outerRule;

public class ReportExecutingStopExecutionFailureUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected ReportExecutingPage page;

    @Before
    public void setUpReportExecutingUiAbstractTest() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        JobExecutionModel jobExecutionModel0 = jobExecutionModel();
        jobExecutionModel0.setExecutionStart(1482836967412l);
        jobExecutionModel0.setJobModel(jobModel);
        jobExecutionModel0.setStatus(ONGOING);
        jobExecutionDbSetUp(jobExecutionModel0);

        JobExecutionModel jobExecutionModel1 = jobExecutionModel();
        jobExecutionModel1.setExecutionStart(1482836933006l);
        jobExecutionModel1.setJobModel(jobModel);
        jobExecutionModel1.setStatus(ONGOING);
        jobExecutionDbSetUp(jobExecutionModel1);

        page = createPage(ReportExecutingPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        if (!page.isRendered()) {
            fail("Could not render executing reports page");
        }

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        page.waitForExecutingReportsList(2);
        page.stopExecution(0);
        page.waitForStopExecutionFailureMessage();
        page.waitForExecutingReportsList(2);
    }
}
