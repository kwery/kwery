package com.kwery.tests.fluentlenium.job.save.add;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.job.save.JobForm;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.dozer.DozerBeanMapper;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_REPORT_NAME_EXISTS_M;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M;
import static com.kwery.tests.util.TestUtil.*;
import static java.text.MessageFormat.format;

public class ReportSaveDuplicateLabelUiTest extends ChromeFluentTest {
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

        jobModel.setSqlQueries(new HashSet<>());
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
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForErrorMessages();

        List<String> expectedErrorMessages = ImmutableList.of(format(JOBAPICONTROLLER_REPORT_NAME_EXISTS_M, jobLabel), format(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M, queryLabel));

        Assert.assertThat(expectedErrorMessages, IsIterableContainingInAnyOrder.containsInAnyOrder(page.getErrorMessages().toArray(new String[2])));
    }
}
