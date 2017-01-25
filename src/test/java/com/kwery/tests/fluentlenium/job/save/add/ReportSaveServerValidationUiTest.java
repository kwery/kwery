package com.kwery.tests.fluentlenium.job.save.add;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SmtpConfiguration;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.job.save.JobForm;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.*;
import static java.text.MessageFormat.format;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ReportSaveServerValidationUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;

    protected String jobLabel = "label";
    protected String queryLabel = "label";

    protected ReportSavePage page;

    protected JobDto jobDto;

    @Before
    public void setUpJobApiControllerSaveJobWithDuplicateLabelTest() {
        jobModel = jobModelWithoutDependents();

        jobDto = jobDtoWithoutId();

        jobDto.setSqlQueries(new LinkedList<>());

        jobModel.setName(jobLabel);
        jobDto.setName(jobModel.getName());

        datasource = datasource();

        SqlQueryModel sqlQueryModel = sqlQueryModel();

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();

        sqlQueryModel.setDatasource(datasource);
        sqlQueryDto.setDatasourceId(datasource.getId());

        sqlQueryModel.setLabel(queryLabel);
        sqlQueryDto.setLabel(queryLabel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobDto.getSqlQueries().add(sqlQueryDto);

        datasourceDbSetup(datasource);
        jobDbSetUp(jobModel);
        sqlQueryDbSetUp(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        page = createPage(ReportSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            TestCase.fail("Could not render report save page");
        }

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() {
        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );

        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);

        String invalidCron = "foo bar moo";

        jobForm.setCronExpression(invalidCron);
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForFailureMessageDisplay();

        List<String> expectedErrorMessages = ImmutableList.of(
                format(JOBAPICONTROLLER_REPORT_NAME_EXISTS_M, jobLabel),
                format(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M, queryLabel),
                format(JOBLABELAPICONTROLLER_INVALID_CRON_EXPRESSION_M, invalidCron)
        );

        assertThat(expectedErrorMessages, containsInAnyOrder(page.getErrorMessages().toArray(new String[2])));
    }
}
