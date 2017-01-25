package com.kwery.tests.fluentlenium.job.save.update.noemailrule;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SmtpConfiguration;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.job.save.add.ReportUpdatePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public abstract class AbstractReportUpdateNoEmailRuleUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    private JobDao jobDao;
    private JobModel jobModel;
    protected ReportUpdatePage page;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(isNoEmailRule())));
        jobDbSetUp(jobModel);

        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        page = createPage(ReportUpdatePage.class);
        page.setReportId(jobModel.getId());
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Failed to render report update page");
        }

        ninjaServerRule.getInjector().getInstance(JobService.class).schedule(jobModel.getId());
        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);
    }

    public abstract boolean isNoEmailRule();

    public void assertNoEmailRule(boolean expected) {
        assertThat(jobDao.getJobById(jobModel.getId()).getRules().get(EMPTY_REPORT_NO_EMAIL), is(String.valueOf(expected)));
    }
}
