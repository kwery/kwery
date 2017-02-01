package com.kwery.tests.fluentlenium.job.save.update;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SmtpConfiguration;
import com.kwery.models.SqlQueryModel;
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
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportUpdateSwitchParentUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Page
    ReportUpdatePage page;
    JobModel parentJobModel0;
    Datasource datasource;
    JobDao jobDao;
    private SqlQueryDao sqlQueryDao;
    private JobModel parentJobModel1;
    private JobModel childJobModel;

    @Before
    public void setUp() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        //Parent Job setup - start
        parentJobModel0 = jobModelWithoutDependents();
        parentJobModel0.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel0);

        parentJobModel0.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel0);

        SqlQueryModel parentSqlQueryModel0 = sqlQueryModel(datasource);
        parentSqlQueryModel0.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(parentSqlQueryModel0);

        parentJobModel0.getSqlQueries().add(parentSqlQueryModel0);
        jobSqlQueryDbSetUp(parentJobModel0);
        //Parent Job setup - end

        //Parent Job setup - start
        parentJobModel1 = jobModelWithoutDependents();
        parentJobModel1.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel1);

        parentJobModel1.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel1);

        SqlQueryModel parentSqlQueryModel1 = sqlQueryModel(datasource);
        parentSqlQueryModel1.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(parentSqlQueryModel1);

        parentJobModel1.getSqlQueries().add(parentSqlQueryModel1);
        jobSqlQueryDbSetUp(parentJobModel1);
        //Parent Job setup - end

        //Child Job setup - start
        childJobModel = jobModelWithoutDependents();
        childJobModel.setCronExpression(null);
        childJobModel.setParentJob(parentJobModel0);
        jobDbSetUp(childJobModel);

        jobDependentDbSetUp(childJobModel);

        childJobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(childJobModel);

        SqlQueryModel childSqlQueryModel = sqlQueryModel(datasource);
        childSqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(childSqlQueryModel);

        childJobModel.getSqlQueries().add(childSqlQueryModel);
        jobSqlQueryDbSetUp(childJobModel);
        //Child Job setup - end

        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        page.go(childJobModel.getId());

        if (!page.isRendered()) {
            fail("Failed to render report update page");
        }

        JobService jobService = ninjaServerRule.getInjector().getInstance(JobService.class);
        jobService.schedule(parentJobModel0.getId());
        jobService.schedule(parentJobModel1.getId());

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);
        sqlQueryDao = ninjaServerRule.getInjector().getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() {
        JobDto jobDto = jobDto();
        jobDto.setCronExpression(null);
        jobDto.setEmails(ImmutableSet.of("grx@bar.com", "brx@boo.com"));
        jobDto.setParentJobId(parentJobModel1.getId());

        SqlQueryDto sqlQueryDto = sqlQueryDto();
        sqlQueryDto.setDatasourceId(datasource.getId());

        jobDto.getSqlQueries().add(sqlQueryDto);

        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );
        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        Map<Integer, String> parentReportIdToLabelMap = ImmutableMap.of(
                parentJobModel0.getId(), parentJobModel0.getName(),
                parentJobModel1.getId(), parentJobModel1.getName()
        );
        page.setParentJobIdToLabelMap(parentReportIdToLabelMap);

        page.waitForModalDisappearance();
        page.waitForReportDisplay(childJobModel.getName());

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.fillAndSubmitReportSaveForm(jobForm);

        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        JobModel savedJobModel = jobDao.getJobByName(jobDto.getName());
        parentJobModel1.setChildJobs(ImmutableSet.of(savedJobModel));
        assertJobModel(savedJobModel, parentJobModel1, jobDto, datasource);

        assertThat(parentJobModel1, theSameBeanAs(jobDao.getJobById(parentJobModel1.getId())));

        parentJobModel0.getChildJobs().clear();
        assertThat(parentJobModel0, theSameBeanAs(jobDao.getJobById(parentJobModel0.getId())));

        assertThat(jobDao.getAllJobs(), hasSize(3));
        assertThat(sqlQueryDao.getAll(), hasSize(3));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
