package com.kwery.tests.fluentlenium.job.executionresult;

import com.google.common.collect.ImmutableList;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    private JobExecutionModel jobExecutionModel;
    private JobModel jobModel;
    private SqlQueryModel insertQuery;
    private SqlQueryModel failedQuery;
    private SqlQueryModel successQuery;
    private SqlQueryExecutionModel insertSqlQueryExecutionModel;
    private SqlQueryExecutionModel failedSqlQueryExecutionModel;
    private SqlQueryExecutionModel successSqlQueryExecutionModel;
    private String jsonResult;

    protected ReportPage page;
    private SqlQueryExecutionModel killedSqlQueryExecutionModel;
    private SqlQueryModel killedQuery;

    @Before
    public void setUp() {
        jobModel = TestUtil.jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);

        Datasource datasource = TestUtil.datasource();
        DbUtil.datasourceDbSetup(datasource);

        insertQuery = TestUtil.sqlQueryModel(datasource);
        insertQuery.setTitle("insert query");
        DbUtil.sqlQueryDbSetUp(insertQuery);

        failedQuery = TestUtil.sqlQueryModel(datasource);
        failedQuery.setTitle("failed query");
        DbUtil.sqlQueryDbSetUp(failedQuery);

        successQuery = TestUtil.sqlQueryModel(datasource);
        successQuery.setTitle("success query");
        DbUtil.sqlQueryDbSetUp(successQuery);

        jobModel.setSqlQueries(ImmutableList.of(insertQuery, failedQuery, successQuery));

        DbUtil.jobSqlQueryDbSetUp(jobModel);

        jobExecutionModel = TestUtil.jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        DbUtil.jobExecutionDbSetUp(jobExecutionModel);

        failedSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        failedSqlQueryExecutionModel.setSqlQuery(failedQuery);
        failedSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        failedSqlQueryExecutionModel.setStatus(SqlQueryExecutionModel.Status.FAILURE);
        failedSqlQueryExecutionModel.setResult("foobarmoo");
        DbUtil.sqlQueryExecutionDbSetUp(failedSqlQueryExecutionModel);

        insertSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        insertSqlQueryExecutionModel.setSqlQuery(insertQuery);
        insertSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        insertSqlQueryExecutionModel.setStatus(SUCCESS);
        insertSqlQueryExecutionModel.setResult(null);
        DbUtil.sqlQueryExecutionDbSetUp(insertSqlQueryExecutionModel);

        successSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        successSqlQueryExecutionModel.setSqlQuery(successQuery);
        successSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        successSqlQueryExecutionModel.setStatus(SUCCESS);
        jsonResult = TestUtil.toJson(ImmutableList.of(
                ImmutableList.of("header0", "header1"),
                ImmutableList.of("foo", "bar"),
                ImmutableList.of("goo", "boo")
        ));
        successSqlQueryExecutionModel.setResult(jsonResult);
        DbUtil.sqlQueryExecutionDbSetUp(successSqlQueryExecutionModel);

        page = createPage(ReportPage.class);
        page.setJobId(jobModel.getId());
        page.setExecutionId(jobExecutionModel.getExecutionId());
        page.setExpectedReportSections(jobModel.getSqlQueries().size());

        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Failed to render report page");
        }
    }

    @Test
    public void test() {
        assertThat(page.reportHeader(), is(jobModel.getTitle()));

        assertThat(page.sectionTitle(0), is(insertQuery.getTitle()));
        assertThat(page.sectionTitle(1), is(failedQuery.getTitle()));
        assertThat(page.sectionTitle(2), is(successQuery.getTitle()));

        assertThat(page.isDownloadLinkPresent(0), is(false));
        assertThat(page.isDownloadLinkPresent(1), is(false));
        assertThat(page.isDownloadLinkPresent(2), is(true));

/*        assertThat(page.isTableDisplayed(0), is(false));
        assertThat(page.isTableDisplayed(1), is(false));
        assertThat(page.isTableDisplayed(2), is(true));*/

        assertThat(page.isTableEmpty(0), is(true));

        assertThat(page.getContent(1), is(failedSqlQueryExecutionModel.getResult()));

        assertThat(page.tableHeaders(2), is(ImmutableList.of("header0", "header1")));
        assertThat(page.tableRows(2, 0), is(ImmutableList.of("foo", "bar")));
        assertThat(page.tableRows(2, 1), is(ImmutableList.of("goo", "boo")));

        assertThat(page.downloadReportLink(2), is(String.format(ninjaServerRule.getServerUrl() + "/api/report/csv/%s", successSqlQueryExecutionModel.getExecutionId())));
    }
}
