package com.kwery.tests.fluentlenium.job.save.update;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobLabelDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.*;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.job.save.add.ReportUpdatePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportUpdateLabelSuccessUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    ReportUpdatePage page;
    JobModel jobModel;
    Datasource datasource;
    JobDao jobDao;
    private SqlQueryDao sqlQueryDao;
    private JobLabelModel jobLabelModel2;
    private JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobDbSetUp(jobModel);

        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        JobLabelModel jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        JobLabelModel jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        jobModel.setLabels(ImmutableSet.of(jobLabelModel0, jobLabelModel1));

        jobJobLabelDbSetUp(jobModel);

        jobLabelModel2 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel2);

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
        sqlQueryDao = ninjaServerRule.getInjector().getInstance(SqlQueryDao.class);
        jobLabelDao = ninjaServerRule.getInjector().getInstance(JobLabelDao.class);

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        page.clickOnRemoveLabel(0);
        List<String> selectedLabels = page.selectedLabels();

        page.clickOnAddLabel(1);
        page.selectLabel(jobLabelModel2.getId(), 1);
        selectedLabels.add(jobLabelModel2.getLabel());

        page.submitReportSaveForm();
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        List<Integer> labelIds = selectedLabels.stream().map(label -> jobLabelDao.getJobLabelModelByLabel(label).getId()).collect(Collectors.toList());
        JobModel saved = jobDao.getJobById(jobModel.getId());
        List<Integer> savedLabels = saved.getLabels().stream().map(JobLabelModel::getId).collect(Collectors.toList());

        assertThat(labelIds, containsInAnyOrder(savedLabels.get(0), savedLabels.get(1)));
    }
}
