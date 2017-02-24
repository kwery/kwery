package com.kwery.tests.fluentlenium.job.executing;

import com.google.common.collect.Lists;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Comparator;
import java.util.List;

import static com.kwery.models.JobExecutionModel.Status.ONGOING;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.junit.rules.RuleChain.outerRule;

public class ReportExecutingStopExecutionFailureUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected ReportExecutingPage page;
    private List<JobExecutionModel> models;
    private JobApiController controller;

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

        controller = ninjaServerRule.getInjector().getInstance(JobApiController.class);

        models = Lists.newArrayList(jobExecutionModel0, jobExecutionModel1);
        models.sort(Comparator.comparing(JobExecutionModel::getExecutionStart).reversed());

        goTo(page);

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        page.stopExecution(0);
        page.assertStopExecutionFailureMessage();
        for (int i = 0; i < models.size(); ++i) {
            page.assertExecutingReports(i, page.toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i))));
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
