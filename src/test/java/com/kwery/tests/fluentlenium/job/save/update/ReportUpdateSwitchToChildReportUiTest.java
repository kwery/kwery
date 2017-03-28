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
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.dozer.DozerBeanMapper;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportUpdateSwitchToChildReportUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Page
    ReportUpdatePage page;

    JobModel parentJobModel;
    Datasource datasource;
    JobDao jobDao;
    private SqlQueryDao sqlQueryDao;
    private JobModel childJobModel;

    @Before
    public void setUp() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        //Parent Job setup - start
        parentJobModel = jobModelWithoutDependents();
        parentJobModel.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel);

        parentJobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel);

        SqlQueryModel parentSqlQueryModel0 = sqlQueryModel(datasource);
        parentSqlQueryModel0.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(parentSqlQueryModel0);

        parentJobModel.getSqlQueries().add(parentSqlQueryModel0);
        jobSqlQueryDbSetUp(parentJobModel);
        //Parent Job setup - end

        //Child Job setup - start
        childJobModel = jobModelWithoutDependents();
        childJobModel.setCronExpression("* * * * *");
        jobDbSetUp(childJobModel);

        childJobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(childJobModel);

        SqlQueryModel childSqlQueryModel = sqlQueryModel(datasource);
        childSqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(childSqlQueryModel);

        childJobModel.getSqlQueries().add(childSqlQueryModel);
        jobSqlQueryDbSetUp(childJobModel);

        JobRuleModel jobRuleModel = jobRuleModel();
        childJobModel.setJobRuleModel(jobRuleModel);
        jobRuleDbSetUp(childJobModel);
        //Child Job setup - end

        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        emailConfigurationDbSet(emailConfiguration());

        page.go(childJobModel.getId());

        if (!page.isRendered()) {
            fail("Failed to render report update page");
        }

        JobService jobService = ninjaServerRule.getInjector().getInstance(JobService.class);
        jobService.schedule(parentJobModel.getId());
        jobService.schedule(childJobModel.getId());

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);
        sqlQueryDao = ninjaServerRule.getInjector().getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() {
        JobDto jobDto = jobDto();
        jobDto.setCronExpression(null);
        jobDto.setParentJobId(parentJobModel.getId());
        jobDto.setEmails(ImmutableSet.of("grx@bar.com", "brx@boo.com"));

        JobRuleModel jobRuleModel = jobRuleModelWithoutId();
        jobRuleModel.setId(childJobModel.getJobRuleModel().getId());
        jobDto.setJobRuleModel(jobRuleModel);

        SqlQueryDto sqlQueryDto = sqlQueryDto();
        sqlQueryDto.setDatasourceId(datasource.getId());

        jobDto.getSqlQueries().add(sqlQueryDto);

        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );
        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        Map<Integer, String> parentReportIdToLabelMap = ImmutableMap.of(
                parentJobModel.getId(), parentJobModel.getName()
        );
        page.setParentJobIdToLabelMap(parentReportIdToLabelMap);

        page.waitForModalDisappearance();
        page.waitForReportDisplay(childJobModel.getName());

        page.chooseParentReport();

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.fillAndSubmitReportSaveForm(jobForm);

        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        JobModel savedJobModel = jobDao.getJobByName(jobDto.getName());
        parentJobModel.getChildJobs().add(jobDao.getJobById(childJobModel.getId()));

        assertJobModel(savedJobModel, parentJobModel, jobDto, datasource);

        assertThat(parentJobModel, theSameBeanAs(jobDao.getJobById(parentJobModel.getId())).excludeProperty("created").excludeProperty("updated"));

        assertThat(jobDao.getAllJobs(), hasSize(2));
        assertThat(sqlQueryDao.getAll(), hasSize(2));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
