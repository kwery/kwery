package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerExecutionResultTest extends AbstractPostLoginApiTest {
    protected String jsonResult;
    protected String executionId = "executionId";
    private SqlQueryModel sqlQueryModel;

    @Before
    public void beforeSqlQueryExecutionResultTest () throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonResult = objectMapper.writeValueAsString(
                ImmutableList.of(
                        ImmutableList.of("header"),
                        ImmutableList.of("value")
                )
        );

        Datasource datasource = TestUtil.datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_ERROR, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, executionId, 1475158740747l, jsonResult, SUCCESS, sqlQueryModel.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .build()
                )
        );
        dbSetup.launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "sqlQueryExecutionResult",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel.getId(),
                        "sqlQueryExecutionId", executionId
                )
        );

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(JsonPath.read(json, "$[0].length()"), is(1));
        assertThat(json, hasJsonPath("$[0].[0]", is("header")));

        assertThat(JsonPath.read(json, "$[1].length()"), is(1));
        assertThat(json, hasJsonPath("$[1].[0]", is("value")));
    }

    @Test
    public void testNonExistent() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "sqlQueryExecutionResult",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel.getId(),
                        "sqlQueryExecutionId", executionId + "foo"
                )
        );

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(jsonResponse, is("[]"));
    }
}
