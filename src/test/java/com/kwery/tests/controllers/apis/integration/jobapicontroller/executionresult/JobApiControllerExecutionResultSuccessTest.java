package com.kwery.tests.controllers.apis.integration.jobapicontroller.executionresult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.*;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import com.kwery.utils.KweryDirectory;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.kwery.models.SqlQueryExecutionModel.Status.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryExecutionDbSetUp;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerExecutionResultSuccessTest extends AbstractPostLoginApiTest {
    private JobExecutionModel jobExecutionModel;
    private JobModel jobModel;
    private SqlQueryModel insertQuery;
    private SqlQueryModel failedQuery;
    private SqlQueryModel successQuery;
    private SqlQueryExecutionModel insertSqlQueryExecutionModel;
    private SqlQueryExecutionModel failedSqlQueryExecutionModel;
    private SqlQueryExecutionModel successSqlQueryExecutionModel;
    private String jsonResult;
    private ImmutableList<String[]> datum;

    @Before
    public void setUp() throws Exception {
        jobModel = TestUtil.jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);

        Datasource datasource = TestUtil.datasource();
        DbUtil.datasourceDbSetup(datasource);

        insertQuery = TestUtil.sqlQueryModel(datasource);
        insertQuery.setTitle("insert query");
        insertQuery.setQuery("insert into foo");
        DbUtil.sqlQueryDbSetUp(insertQuery);

        failedQuery = TestUtil.sqlQueryModel(datasource);
        failedQuery.setTitle("failed query");
        DbUtil.sqlQueryDbSetUp(failedQuery);

        successQuery = TestUtil.sqlQueryModel(datasource);
        successQuery.setTitle("success query");
        DbUtil.sqlQueryDbSetUp(successQuery);

        SqlQueryModel killedSqlQuery = TestUtil.sqlQueryModel(datasource);
        killedSqlQuery.setTitle("killed query");
        DbUtil.sqlQueryDbSetUp(killedSqlQuery);

        jobModel.setSqlQueries(ImmutableList.of(insertQuery, failedQuery, successQuery, killedSqlQuery));

        DbUtil.jobSqlQueryDbSetUp(jobModel);

        jobExecutionModel = TestUtil.jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        DbUtil.jobExecutionDbSetUp(jobExecutionModel);

        failedSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        failedSqlQueryExecutionModel.setSqlQuery(failedQuery);
        failedSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        failedSqlQueryExecutionModel.setStatus(SqlQueryExecutionModel.Status.FAILURE);
        failedSqlQueryExecutionModel.setExecutionError("foobarmoo");
        sqlQueryExecutionDbSetUp(failedSqlQueryExecutionModel);

        insertSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        insertSqlQueryExecutionModel.setSqlQuery(insertQuery);
        insertSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        insertSqlQueryExecutionModel.setStatus(SUCCESS);
        insertSqlQueryExecutionModel.setExecutionError(null);
        sqlQueryExecutionDbSetUp(insertSqlQueryExecutionModel);

        successSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        successSqlQueryExecutionModel.setSqlQuery(successQuery);
        successSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        successSqlQueryExecutionModel.setStatus(SUCCESS);
        KweryDirectory kweryDirectory = getInjector().getInstance(KweryDirectory.class);
        File csv = kweryDirectory.createFile();
        datum = ImmutableList.of(
                new String[]{"header0", "header1"},
                new String[]{"foo", "bar"},
                new String[]{"goo", "boo"}
        );
        TestUtil.writeCsv(datum, csv);
        successSqlQueryExecutionModel.setResultFileName(csv.getName());
        sqlQueryExecutionDbSetUp(successSqlQueryExecutionModel);

        SqlQueryExecutionModel killedSqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
        killedSqlQueryExecutionModel.setSqlQuery(killedSqlQuery);
        killedSqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        killedSqlQueryExecutionModel.setStatus(KILLED);
        killedSqlQueryExecutionModel.setExecutionError(null);
        sqlQueryExecutionDbSetUp(killedSqlQueryExecutionModel);
    }

    @Test
    public void test() throws IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "jobExecutionResult",
                ImmutableMap.of("jobExecutionId", jobExecutionModel.getExecutionId()));
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        assertThat(response, JsonPathMatchers.isJson());
        assertThat(response, hasJsonPath("$.title", is(jobModel.getTitle())));

        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[0].title", is(insertQuery.getTitle())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[0].status", is(SUCCESS.name())));

        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[1].title", is(failedQuery.getTitle())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[1].status", is(FAILURE.name())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[1].errorResult", is(failedSqlQueryExecutionModel.getExecutionError())));

        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[2].title", is(successQuery.getTitle())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[2].status", is(SUCCESS.name())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[2].executionId", is(successSqlQueryExecutionModel.getExecutionId())));

        for (int row = 0; row < datum.size(); ++row) {
            String[] rowDatum = datum.get(row);
            for (int col = 0; col < rowDatum.length; ++col) {
                assertThat(response, hasJsonPath(String.format("$.sqlQueryExecutionResultDtos[2].jsonResult[%d][%d]", row, col), is(datum.get(row)[col])));
            }
        }
    }
}
