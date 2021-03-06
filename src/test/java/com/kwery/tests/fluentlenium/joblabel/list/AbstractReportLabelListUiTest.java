package com.kwery.tests.fluentlenium.joblabel.list;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.junit.rules.RuleChain.outerRule;

public abstract class AbstractReportLabelListUiTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    ReportLabelListPage page;
    JobLabelDao jobLabelDao;

    JobLabelModel parentJobLabelModel;
    JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        jobLabelModel = jobLabelModel();
        jobLabelModel.setParentLabel(parentJobLabelModel);
        jobLabelDbSetUp(jobLabelModel);

        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobModel.getLabels().add(parentJobLabelModel);
        jobJobLabelDbSetUp(jobModel);

        page.go();

        page.waitForModalDisappearance();

        jobLabelDao = ninjaServerRule.getInjector().getInstance(JobLabelDao.class);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
