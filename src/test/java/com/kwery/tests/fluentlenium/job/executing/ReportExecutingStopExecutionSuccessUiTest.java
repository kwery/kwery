package com.kwery.tests.fluentlenium.job.executing;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.rules.RuleChain.outerRule;

public class ReportExecutingStopExecutionSuccessUiTest extends ChromeFluentTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected ReportExecutingPage page;
    private Datasource datasource;

    @Before
    public void setUp() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        setUpJob();
        setUpJob();

        JobExecutionDao jobExecutionDao = ninjaServerRule.getInjector().getInstance(JobExecutionDao.class);

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(JobExecutionModel.Status.ONGOING));

        waitAtMost(TIMEOUT_SECONDS, SECONDS).until(() -> jobExecutionDao.filter(filter).size() >= 2);

        goTo(page);
        if (!page.isRendered()) {
            fail("Could not render executing reports page");
        }

        page.waitForModalDisappearance();
    }

    private void setUpJob() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setQuery("select sleep(1000000)");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        ninjaServerRule.getInjector().getInstance(JobService.class).launch(jobModel.getId());

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobModel.getId());
    }

    @Test
    public void test() {
        page.waitForExecutingReportsList(2);
        page.stopExecution(0);
        page.waitForStopExecutionSuccessMessage();
        page.waitForExecutingReportsList(1);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
