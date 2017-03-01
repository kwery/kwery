package com.kwery.tests.fluentlenium.job.executing;

import com.google.common.collect.Lists;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
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
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.junit.rules.RuleChain.outerRule;

public class ReportExecutingListUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected JobApiController controller = new JobApiController(null, null, null, null, null, null, null, null, null, null, null);

    @Page
    protected ReportExecutingPage page;
    protected JobExecutionModel jobExecutionModel0;
    protected JobExecutionModel jobExecutionModel1;
    private List<JobExecutionModel> models;

    @Before
    public void setUpReportExecutingUiAbstractTest() {
        /*
            1482836967412 - Tue Dec 27 16:39:27 IST 2016
            1482836933006 - Tue Dec 27 16:38:53 IST 2016
         */
        JobModel jobModel = jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);

        jobExecutionModel0 = jobExecutionModel();
        jobExecutionModel0.setExecutionStart(1482836967412l);
        jobExecutionModel0.setJobModel(jobModel);
        jobExecutionModel0.setStatus(ONGOING);
        jobExecutionDbSetUp(jobExecutionModel0);

        jobExecutionModel1 = jobExecutionModel();
        jobExecutionModel1.setExecutionStart(1482836933006l);
        jobExecutionModel1.setJobModel(jobModel);
        jobExecutionModel1.setStatus(ONGOING);
        jobExecutionDbSetUp(jobExecutionModel1);

        models = Lists.newArrayList(jobExecutionModel0, jobExecutionModel1);
        models.sort(Comparator.comparing(JobExecutionModel::getExecutionStart).reversed());

        goTo(page);
        page.waitForModalDisappearance();
    }

    @Test
    public void test() throws Exception {
        for (int i = 0; i < models.size(); ++i) {
            page.assertExecutingReports(i, page.toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i))));
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
