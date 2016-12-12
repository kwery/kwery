package com.kwery.tests.fluentlenium.job;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import junit.framework.TestCase;
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

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobModel.JOB_SQL_QUERY_TABLE;
import static com.kwery.models.JobModel.SQL_QUERY_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.ID_COLUMN;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_REPORT_LABEL_EXISTS_M;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
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

        jobModel.setLabel(jobLabel);
        jobDto.setLabel(jobModel.getLabel());

        datasource = datasource();

        SqlQueryModel sqlQueryModel = sqlQueryModel();

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();

        sqlQueryModel.setDependentQueries(null);
        sqlQueryModel.setRecipientEmails(null);
        sqlQueryModel.setCronExpression(null);

        sqlQueryModel.setDatasource(datasource);
        sqlQueryDto.setDatasourceId(datasource.getId());

        sqlQueryModel.setLabel(queryLabel);
        sqlQueryDto.setLabel(queryLabel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobDto.getSqlQueries().add(sqlQueryDto);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        jobDbSetUp(jobModel);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        Operations.insertInto(SQL_QUERY_TABLE)
                                .row()
                                .column(ID_COLUMN, sqlQueryModel.getId())
                                .column(SqlQueryModel.LABEL_COLUMN, sqlQueryModel.getLabel())
                                .column(SqlQueryModel.QUERY_COLUMN, sqlQueryModel.getQuery())
                                .column(SqlQueryModel.DATASOURCE_ID_FK_COLUMN, sqlQueryModel.getDatasource().getId())
                                .end()
                                .build(),
                        Operations.insertInto(JOB_SQL_QUERY_TABLE)
                                .row()
                                .column(JobModel.ID_COLUMN, 1)
                                .column(JobModel.JOB_ID_FK_COLUMN, jobModel.getId())
                                .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                .end()
                                .build()
                )
        ).launch();

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

        page.fillAndSubmitReportSaveForm(jobDto);
        page.waitForErrorMessages();

        List<String> expectedErrorMessages = ImmutableList.of(format(JOBAPICONTROLLER_REPORT_LABEL_EXISTS_M, jobLabel), format(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M, queryLabel));

        Assert.assertThat(expectedErrorMessages, IsIterableContainingInAnyOrder.containsInAnyOrder(page.getErrorMessages().toArray(new String[2])));
    }
}
