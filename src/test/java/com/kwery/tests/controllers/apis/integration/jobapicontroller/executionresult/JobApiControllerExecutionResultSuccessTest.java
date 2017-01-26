package com.kwery.tests.controllers.apis.integration.jobapicontroller.executionresult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.*;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import net.minidev.json.JSONArray;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.kwery.models.SqlQueryExecutionModel.Status.FAILURE;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

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
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[1].errorResult", is(failedSqlQueryExecutionModel.getResult())));

        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[2].title", is(successQuery.getTitle())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[2].status", is(SUCCESS.name())));
        assertThat(response, hasJsonPath("$.sqlQueryExecutionResultDtos[2].executionId", is(successSqlQueryExecutionModel.getExecutionId())));

        JSONArray jsonArray = JsonPath.read(response, "$.sqlQueryExecutionResultDtos[2].jsonResult");
        assertThat(TestUtil.toJson(jsonArray), sameJSONAs(jsonResult));
    }
}
