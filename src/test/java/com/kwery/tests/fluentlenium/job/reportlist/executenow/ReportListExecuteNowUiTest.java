package com.kwery.tests.fluentlenium.job.reportlist.executenow;

import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.job.reportlist.ReportListPage;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static junit.framework.TestCase.fail;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;

@RunWith(Parameterized.class)
public class ReportListExecuteNowUiTest extends ChromeFluentTest {
    protected boolean waitingTest;

    @Parameters(name = "waitingTest={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false}
        });
    }

    public ReportListExecuteNowUiTest(boolean waitingTest) {
        this.waitingTest = waitingTest;
    }

    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected ReportListPage page;

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();
    private JobModel jobModel;

    @Before
    public void setUp() {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(DbUtil.dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel();
        sqlQueryModel.setDatasource(datasource);

        if (waitingTest) {
            sqlQueryModel.setQuery("select sleep(100000)");
        } else {
            sqlQueryModel.setQuery("select User from mysql.user where User = 'root'");
        }


        sqlQueryDbSetUp(sqlQueryModel);

        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobModel.getSqlQueries().add(sqlQueryModel);

        jobSqlQueryDbSetUp(jobModel);

        JobService jobService = ninjaServerRule.getInjector().getInstance(JobService.class);
        jobService.schedule(jobModel.getId());

        page.go(1);

        if (!page.isRendered()) {
            fail("Failed to render report list page");
        }

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        page.generateNow(0);

        if (waitingTest) {
            page.assertWaitingMessage();
        } else {
            assertThat(el("div", withClass("report-section-f"))).isDisplayed();
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
