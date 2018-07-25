package com.kwery.tests.fluentlenium.job.save.add;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.*;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.job.save.JobForm;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.testcontainers.containers.MySQLContainer;

import java.util.ArrayList;
import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportSaveWithDependentsSuccessUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    @Page
    protected ReportSavePage page;

    protected JobDto jobDto;

    protected Datasource datasource;

    JobDao jobDao;
    private JobModel parentJobModel;

    @Before
    public void setUpReportSaveSuccessUiTest() {
        datasource = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setEmails(ImmutableSet.of("foo@bar.com", "moo@bar.com"));

        JobRuleModel jobRuleModel = jobRuleModelWithoutId();
        jobDto.setJobRuleModel(jobRuleModel);

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
            sqlQueryDto.setQuery("select * from mysql.user");
            sqlQueryDto.setDatasourceId(datasource.getId());

            jobDto.getSqlQueries().add(sqlQueryDto);
        }

        parentJobModel = jobModelWithoutDependents();
        jobDbSetUp(parentJobModel);

        SqlQueryModel parentSqlQueryModel = sqlQueryModel(datasource);
        parentSqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(parentSqlQueryModel);

        parentJobModel.getSqlQueries().add(parentSqlQueryModel);

        jobSqlQueryDbSetUp(parentJobModel);

        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        emailConfigurationDbSet(emailConfiguration());

        ninjaServerRule.getInjector().getInstance(JobService.class).schedule(parentJobModel.getId());

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);

        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        page.chooseParentReport();

        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );
        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        Map<Integer, String> parentReportIdToLabelMap = ImmutableMap.of(
                parentJobModel.getId(), parentJobModel.getName()
        );
        page.setParentJobIdToLabelMap(parentReportIdToLabelMap);

        jobDto.setCronExpression("");
        jobDto.setParentJobId(parentJobModel.getId());

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        JobModel savedJobModel = jobDao.getJobByName(jobDto.getName());
        parentJobModel.setChildJobs(ImmutableSet.of(savedJobModel));
        assertJobModel(savedJobModel, parentJobModel, jobDto, datasource);

        assertThat(parentJobModel, theSameBeanAs(jobDao.getJobById(parentJobModel.getId())).excludeProperty("updated").excludeProperty("created"));

        assertThat(jobDao.getAllJobs(), hasSize(2));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
