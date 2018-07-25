package com.kwery.tests.fluentlenium.job.executionlist;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionDto;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.ReportExecution;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kwery.models.JobExecutionModel.Status.*;
import static com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.ReportExecution.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.junit.rules.RuleChain.outerRule;

@RunWith(Parameterized.class)
public abstract class AbstractReportExecutionListUiTest extends ChromeFluentTest {
    @Parameter(0)
    public boolean topPagination;

    @Parameter(1)
    public boolean bottomPagination;

    @Parameters(name = "topPagination={0}, bottomPagination={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, false},
                {false, true}
        });
    }

    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    protected int resultCount;

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected ReportExecutionListPage page;
    protected JobModel jobModel;
    protected JobExecutionModel jem0;
    protected JobExecutionModel jem1;
    protected JobExecutionModel jem2;
    protected JobExecutionModel jem3;

    protected JobApiController controller;

    protected List<JobExecutionModel> models;

    public void setUp() throws Exception {
        Preconditions.checkState(topPagination != bottomPagination, "topPagination and bottomPagination cannot have the same value");

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

        models = Lists.newArrayList(jem0, jem1, jem2, jem3);
        models.sort(Comparator.comparing(JobExecutionModel::getExecutionStart).reversed());

        controller = ninjaServerRule.getInjector().getInstance(JobApiController.class);

        page.go(jobModel.getId(), getResultCount());

/*        if (!page.isRendered()) {
            fail("Could not render report execution list page");
        }*/


        page.waitForModalDisappearance();
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

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    public Map<ReportExecution, ?> toMap(JobExecutionDto jobExecutionDto, JobModel jobModel) {
        Map map = new HashMap();
        map.put(start, jobExecutionDto.getStart());
        map.put(end, jobExecutionDto.getEnd());
        map.put(status, jobExecutionDto.getStatus());

        if (SUCCESS.name().equals(jobExecutionDto.getStatus()) || FAILURE.name().equals(jobExecutionDto.getStatus())) {
            map.put(reportLink, String.format("/#report/%d/execution/%s", jobModel.getId(), jobExecutionDto.getExecutionId()));
        }

        return map;
    }

    protected ReportExecutionListPage.PaginationPosition getPaginationPosition() {
        if (topPagination) {
            return ReportExecutionListPage.PaginationPosition.top;
        }

        return ReportExecutionListPage.PaginationPosition.bottom;
    }
}
