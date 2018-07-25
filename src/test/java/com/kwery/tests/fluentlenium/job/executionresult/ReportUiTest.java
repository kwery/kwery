package com.kwery.tests.fluentlenium.job.executionresult;

import com.google.common.collect.ImmutableList;
import com.kwery.conf.KweryDirectory;
import com.kwery.models.*;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import com.kwery.utils.KweryConstant;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.File;
import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING_M;
import static com.kwery.tests.util.TestUtil.*;
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
    private SqlQueryModel warningQuery;

    private SqlQueryExecutionModel insertSqlQueryExecutionModel;
    private SqlQueryExecutionModel failedSqlQueryExecutionModel;
    private SqlQueryExecutionModel successSqlQueryExecutionModel;
    private SqlQueryExecutionModel warningSqlQueryExecutionModel;

    @Page
    protected ReportPage page;

    @Before
    public void setUp() throws Exception {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        insertQuery = sqlQueryModel(datasource);
        insertQuery.setTitle("insert query");
        insertQuery.setQuery("insert into foo");
        sqlQueryDbSetUp(insertQuery);

        failedQuery = sqlQueryModel(datasource);
        failedQuery.setTitle("failed query");
        sqlQueryDbSetUp(failedQuery);

        successQuery = sqlQueryModel(datasource);
        successQuery.setTitle("success query");
        sqlQueryDbSetUp(successQuery);

        warningQuery = sqlQueryModel(datasource);
        warningQuery.setTitle("warning query");
        sqlQueryDbSetUp(warningQuery);

        jobModel.setSqlQueries(ImmutableList.of(insertQuery, failedQuery, successQuery, warningQuery));

        jobSqlQueryDbSetUp(jobModel);

        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionDbSetUp(jobExecutionModel);

        failedSqlQueryExecutionModel = sqlQueryExecutionModel();
        failedSqlQueryExecutionModel.setSqlQuery(failedQuery);
        failedSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        failedSqlQueryExecutionModel.setStatus(SqlQueryExecutionModel.Status.FAILURE);
        failedSqlQueryExecutionModel.setExecutionError("foobarmoo");
        sqlQueryExecutionDbSetUp(failedSqlQueryExecutionModel);

        insertSqlQueryExecutionModel = sqlQueryExecutionModel();
        insertSqlQueryExecutionModel.setSqlQuery(insertQuery);
        insertSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        insertSqlQueryExecutionModel.setStatus(SUCCESS);
        insertSqlQueryExecutionModel.setExecutionError(null);
        sqlQueryExecutionDbSetUp(insertSqlQueryExecutionModel);

        successSqlQueryExecutionModel = sqlQueryExecutionModel();
        successSqlQueryExecutionModel.setSqlQuery(successQuery);
        successSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        successSqlQueryExecutionModel.setStatus(SUCCESS);

        KweryDirectory kweryDirectory = ninjaServerRule.getInjector().getInstance(KweryDirectory.class);
        File csv = kweryDirectory.createFile();

        List<String[]> datum = ImmutableList.of(
                new String[]{"header0", "header1"},
                new String[]{"foo", "bar"},
                new String[]{"goo", "boo"}
        );

        TestUtil.writeCsv(datum, csv);

        successSqlQueryExecutionModel.setExecutionError(null);
        successSqlQueryExecutionModel.setResultFileName(csv.getName());

        sqlQueryExecutionDbSetUp(successSqlQueryExecutionModel);

        warningSqlQueryExecutionModel = sqlQueryExecutionModel();
        warningSqlQueryExecutionModel.setSqlQuery(warningQuery);
        warningSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        warningSqlQueryExecutionModel.setStatus(SUCCESS);
        warningSqlQueryExecutionModel.setExecutionError(null);

        File hugeCsv = kweryDirectory.createFile();
        TestUtil.writeCsvOfLines(KweryConstant.SQL_QUERY_RESULT_DISPLAY_ROW_LIMIT, hugeCsv);

        warningSqlQueryExecutionModel.setResultFileName(hugeCsv.getName());

        sqlQueryExecutionDbSetUp(warningSqlQueryExecutionModel);

        page = newInstance(ReportPage.class);
        page.setJobId(jobModel.getId());
        page.setExecutionId(jobExecutionModel.getExecutionId());
        page.setExpectedReportSections(jobModel.getSqlQueries().size());

        page.go(jobModel.getId(), jobExecutionModel.getExecutionId());

        if (!page.isRendered()) {
            fail("Failed to render report page");
        }

        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        assertThat(page.reportHeader(), is(jobModel.getTitle()));

        assertThat(page.sectionTitle(0), is(insertQuery.getTitle()));
        assertThat(page.sectionTitle(1), is(failedQuery.getTitle()));
        assertThat(page.sectionTitle(2), is(successQuery.getTitle()));
        assertThat(page.sectionTitle(3), is(warningQuery.getTitle()));

        assertThat(page.isDownloadLinkPresent(0), is(false));
        assertThat(page.isDownloadLinkPresent(1), is(false));
        assertThat(page.isDownloadLinkPresent(2), is(true));
        assertThat(page.isDownloadLinkPresent(3), is(true));

        assertThat(page.isTableEmpty(0), is(true));

        assertThat(page.getFailureContent(1), is(failedSqlQueryExecutionModel.getExecutionError()));

        assertThat(page.tableHeaders(2), is(ImmutableList.of("header0", "header1")));
        assertThat(page.tableRows(2, 0), is(ImmutableList.of("foo", "bar")));
        assertThat(page.tableRows(2, 1), is(ImmutableList.of("goo", "boo")));

        assertThat(page.downloadReportLink(2), is(String.format(ninjaServerRule.getServerUrl() + "/api/report/csv/%s", successSqlQueryExecutionModel.getExecutionId())));

        assertThat(page.getWarningContent(3), is(JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING_M));

        assertThat(page.downloadReportLink(3), is(String.format(ninjaServerRule.getServerUrl() + "/api/report/csv/%s", warningSqlQueryExecutionModel.getExecutionId())));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
