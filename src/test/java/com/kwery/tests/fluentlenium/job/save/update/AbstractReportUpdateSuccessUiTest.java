package com.kwery.tests.fluentlenium.job.save.update;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.*;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.job.save.JobForm;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.*;
import org.dozer.DozerBeanMapper;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.testcontainers.containers.MySQLContainer;

import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public abstract class AbstractReportUpdateSuccessUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();
    protected boolean skipDaoCheck = false;

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    @Rule
    public WiserRule wiserRule = new WiserRule();

    String email0 = "foo@bar.com";
    String email1 = "boo@goo.com";
    String email2 = "moo@goo.com";

    @Page
    protected ReportUpdatePage page;

    protected JobModel jobModel;
    Datasource datasource;
    protected JobDao jobDao;
    private SqlQueryDao sqlQueryDao;

    @Before
    public void setUp() {
        page.setCopy(getCopy());

        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobDbSetUp(jobModel);

        JobRuleModel jobRuleModel = jobRuleModel();
        jobModel.setJobRuleModel(jobRuleModel);

        jobRuleDbSetUp(jobModel);

        jobModel.setEmails(ImmutableSet.of(email0));
        jobEmailDbSetUp(jobModel);

        datasource = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(sqlQueryModel);

        SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModel();
        sqlQueryModel.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel);
        sqlQueryEmailSettingDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        DbUtil.emailConfigurationDbSet(emailConfiguration());

        page.go(jobModel.getId());

        if (!page.isRendered()) {
            fail("Failed to render report update page");
        }

        ninjaServerRule.getInjector().getInstance(JobService.class).schedule(jobModel.getId());

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);
        sqlQueryDao = ninjaServerRule.getInjector().getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() {
        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );
        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        page.waitForModalDisappearance();
        page.waitForReportDisplay(jobModel.getName());

        JobDto jobDto = jobDto();
        jobDto.setCronExpression("* * * * *");
        jobDto.setEmails(ImmutableSet.of(email1, email2));

        JobRuleModel jobRuleModel = TestUtil.jobRuleModelWithoutId();
        jobRuleModel.setId(jobModel.getId());
        jobDto.setJobRuleModel(jobRuleModel);

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDto();
            sqlQueryDto.setQuery("select * from mysql.user");
            sqlQueryDto.setDatasourceId(datasource.getId());

            SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModel();
            sqlQueryDto.setSqlQueryEmailSetting(sqlQueryEmailSettingModel);

            jobDto.getSqlQueries().add(sqlQueryDto);
        }

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.fillAndSubmitReportSaveForm(jobForm);

        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        if (!skipDaoCheck) {
            if (getCopy()) {
                assertThat(jobDao.getAllJobs(), hasSize(2));
                assertThat(sqlQueryDao.getAll(), hasSize(3));
            } else {
                assertThat(jobDao.getAllJobs(), hasSize(1));
                assertThat(sqlQueryDao.getAll(), hasSize(2));
            }
            assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    public void setSkipDaoCheck(boolean skipDaoCheck) {
        this.skipDaoCheck = skipDaoCheck;
    }

    public abstract boolean getCopy();
}
