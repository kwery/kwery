package com.kwery.tests.fluentlenium.job.executionlist;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.kwery.models.JobExecutionModel.Status.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static junit.framework.TestCase.fail;
import static org.junit.rules.RuleChain.outerRule;

public abstract class AbstractReportExecutionListUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();
    protected int resultCount;

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected ReportExecutionListPage page;
    protected JobModel jobModel;
    protected JobExecutionModel jem0;
    protected JobExecutionModel jem1;
    protected JobExecutionModel jem2;
    protected JobExecutionModel jem3;

    public void setUp() throws Exception {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jem0 = jobExecutionModel();
        jem0.setExecutionStart(toEpoch("Sat Jan 07 2017 05:10"));
        jem0.setExecutionEnd(toEpoch("Sat Jan 07 2017 05:20"));
        jem0.setStatus(SUCCESS);
        jem0.setJobModel(jobModel);
        jobExecutionDbSetUp(jem0);

        jem1 = jobExecutionModel();
        jem1.setExecutionStart(toEpoch("Sat Jan 07 2017 05:30"));
        jem1.setExecutionEnd(toEpoch("Sat Jan 07 2017 05:40"));
        jem1.setStatus(FAILURE);
        jem1.setJobModel(jobModel);
        jobExecutionDbSetUp(jem1);

        jem2 = jobExecutionModel();
        jem2.setExecutionStart(toEpoch("Sat Jan 07 2017 05:50"));
        jem2.setExecutionEnd(toEpoch("Sat Jan 07 2017 06:00"));
        jem2.setStatus(KILLED);
        jem2.setJobModel(jobModel);
        jobExecutionDbSetUp(jem2);

        jem3 = jobExecutionModel();
        jem3.setExecutionStart(toEpoch("Sat Jan 07 2017 06:10"));
        jem3.setExecutionEnd(null);
        jem3.setStatus(ONGOING);
        jem3.setJobModel(jobModel);
        jobExecutionDbSetUp(jem3);

        page = createPage(ReportExecutionListPage.class);

        page.setJobId(jobModel.getId());
        page.setResultCount(getResultCount());

        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report execution list page");
        }
    }

    protected long toEpoch(String date) throws ParseException {
        return new SimpleDateFormat(JobApiController.DISPLAY_DATE_FORMAT).parse(date).getTime();
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
