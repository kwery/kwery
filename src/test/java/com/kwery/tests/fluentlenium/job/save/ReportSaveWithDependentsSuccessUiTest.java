package com.kwery.tests.fluentlenium.job.save;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.ArrayList;
import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportSaveWithDependentsSuccessUiTest extends ReportSaveSuccessUiTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected ReportSavePage page;

    protected JobDto jobDto;

    protected Datasource datasource;

    JobDao jobDao;
    private JobModel parentJobModel;

    @Before
    public void setUpReportSaveSuccessUiTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setEmails(ImmutableSet.of("foo@bar.com", "moo@bar.com"));

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

        ninjaServerRule.getInjector().getInstance(JobService.class).schedule(parentJobModel.getId());

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);

        page = createPage(ReportSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }
    }

    @Test
    public void test() {
        page.toggleParentReport();
        page.waitUntilParentReportIsEnabled();

        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );
        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        Map<Integer, String> parentReportIdToLabelMap = ImmutableMap.of(
                parentJobModel.getId(), parentJobModel.getLabel()
        );
        page.setParentJobIdToLabelMap(parentReportIdToLabelMap);

        jobDto.setCronExpression("");
        jobDto.setParentJobId(parentJobModel.getId());

        page.fillAndSubmitReportSaveForm(jobDto);
        page.waitForReportSaveSuccessMessage();

        JobModel savedJobModel = jobDao.getJobByLabel(jobDto.getLabel());
        parentJobModel.setChildJobs(ImmutableSet.of(savedJobModel));
        assertJobModel(savedJobModel, parentJobModel, jobDto, datasource);

        assertThat(parentJobModel, theSameBeanAs(jobDao.getJobById(parentJobModel.getId())));

        assertThat(jobDao.getAllJobs(), hasSize(2));
    }
}
