package com.kwery.tests.fluentlenium.job.reportlist;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.*;
import com.kwery.services.job.JobService;
import com.kwery.services.search.SearchIndexer;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;
import java.util.stream.Collectors;

import static com.kwery.tests.fluentlenium.job.reportlist.ReportListPage.ReportList.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;

@RunWith(Parameterized.class)
public class AbstractReportListUiTest extends ChromeFluentTest {
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
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected ReportListPage page;

    protected String parentJobName;

    protected Datasource datasource;
    protected JobLabelModel jobLabelModel;

    protected List<JobModel> jobModels;

    protected JobModel jobModel;
    protected JobModel parentJob;
    protected JobModel childJob;
    private JobApiController jobApiController;

    protected String searchString = "foo";
    protected List<JobModel> expectedSearchOrder;

    @Before
    public void setUp() {
        Preconditions.checkState(topPagination != bottomPagination, "topPagination and bottomPagination cannot have the same value");

        datasource = datasource();

        datasource.setLabel(searchString);
        datasourceDbSetup(datasource);

        jobModel = jobModelWithoutDependents();
        jobModel.setTitle(searchString);

        JobExecutionModel jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobDbSetUp(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);
        setSqlQueryModel(jobModel);

        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);
        jobModel.getLabels().add(jobLabelModel);
        jobJobLabelDbSetUp(jobModel);

        parentJob = jobModelWithoutDependents();
        parentJob.setName(searchString);

        parentJobName = parentJob.getName();
        jobDbSetUp(parentJob);
        parentJob.getLabels().add(jobLabelModel);
        setSqlQueryModel(parentJob);
        jobJobLabelDbSetUp(parentJob);

        JobExecutionModel parentJobExecutionModel = jobExecutionModel();
        parentJobExecutionModel.setJobModel(parentJob);
        jobExecutionDbSetUp(parentJobExecutionModel);

        childJob = jobModelWithoutDependents();
        childJob.setCronExpression("");
        childJob.setParentJob(parentJob);
        jobDbSetUp(childJob);
        jobDependentDbSetUp(childJob);
        setSqlQueryModel(childJob);

        JobExecutionModel childJobExecutionModel = jobExecutionModel();
        childJobExecutionModel.setJobModel(childJob);
        jobExecutionDbSetUp(childJobExecutionModel);

        JobService jobService = ninjaServerRule.getInjector().getInstance(JobService.class);
        jobService.schedule(parentJob.getId());
        jobService.schedule(jobModel.getId());

        jobApiController = ninjaServerRule.getInjector().getInstance(JobApiController.class);

        page.go(getResultCount());

        if (!page.isRendered()) {
            fail("Could not render report list page");
        }

        jobModels = Lists.newArrayList(jobModel, parentJob, childJob);
        jobModels.sort(Comparator.comparing(JobModel::getUpdated).reversed());

        expectedSearchOrder = ImmutableList.of(jobModel, parentJob, childJob);

        //Since we save job models directly bypassing hibernate
        ninjaServerRule.getInjector().getInstance(SearchIndexer.class).index();
    }

    void setSqlQueryModel(JobModel jobModel) {
        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);
        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);
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

    protected Map<ReportListPage.ReportList, ?> toReportRowMap(JobModel jobModel) {
        JobModelHackDto dto = jobApiController.toJobModelHackDto(jobModel);

        Map map = new HashMap();
        map.put(title, jobModel.getTitle());
        map.put(name, jobModel.getName());
        map.put(lastExecution, dto.getLastExecution());
        map.put(nextExecution, dto.getNextExecution());
        map.put(labels, jobModel.getLabels().stream().map(JobLabelModel::getLabel).collect(Collectors.toList()));
        map.put(reportEditLink, String.format("/#report/%d", jobModel.getId()));
        map.put(executionsLink, String.format("/#report/%d/execution-list", jobModel.getId()));
        map.put(copyLink, String.format("/#report/%d/copy", jobModel.getId()));

        return map;
    }

    protected List<JobModel> removeJobModel(JobModel jobModel) {
        List<JobModel> modifiedJobModels = new ArrayList<>(jobModels);
        modifiedJobModels.removeIf(jobModel1 -> jobModel1.getName().equals(jobModel.getName()));
        return modifiedJobModels;
    }

    protected ReportListPage.PaginationPosition getPaginationPosition() {
        if (topPagination) {
            return ReportListPage.PaginationPosition.top;
        }

        return ReportListPage.PaginationPosition.bottom;
    }
}
