package com.kwery.tests.fluentlenium.job.reportlist;

import com.kwery.models.Datasource;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import java.util.HashMap;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;

public class AbstractReportListUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();
    protected int resultCount;

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    ReportListPage page;

    Map<String, ReportListRow> rowMap = new HashMap<>();

    String parentJobName;
    private Datasource datasource;
    JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);
        setSqlQueryModel(jobModel);

        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);
        jobModel.getLabels().add(jobLabelModel);
        jobJobLabelDbSetUp(jobModel);

        JobModel parentJob = jobModelWithoutDependents();
        parentJobName = parentJob.getName();
        jobDbSetUp(parentJob);
        parentJob.getLabels().add(jobLabelModel);
        setSqlQueryModel(parentJob);
        jobJobLabelDbSetUp(parentJob);

        JobModel childJob = jobModelWithoutDependents();
        childJob.setCronExpression("");
        childJob.setParentJob(parentJob);
        jobDbSetUp(childJob);
        jobDependentDbSetUp(childJob);
        setSqlQueryModel(childJob);

        JobService jobService = ninjaServerRule.getInjector().getInstance(JobService.class);
        jobService.schedule(parentJob.getId());
        jobService.schedule(jobModel.getId());

        page = createPage(ReportListPage.class);
        page.setResultCount(getResultCount());

        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report list page");
        }

        rowMap.put(jobModel.getName(), toRow(jobModel));
        rowMap.put(parentJob.getName(), toRow(parentJob));
        rowMap.put(childJob.getName(), toRow(childJob));
    }

    void setSqlQueryModel(JobModel jobModel) {
        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);
        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);
    }

    ReportListRow toRow(JobModel jobModel) {
        ReportListRow row = new ReportListRow();

        row.setLabel(jobModel.getName());
        row.setReportLink(String.format(ninjaServerRule.getServerUrl() + "/#report/%d", jobModel.getId()));
        row.setCronExpression(jobModel.getCronExpression());
        row.setExecutionListLink(String.format(ninjaServerRule.getServerUrl() + "/#report/%d/execution-list", jobModel.getId()));

        return row;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
