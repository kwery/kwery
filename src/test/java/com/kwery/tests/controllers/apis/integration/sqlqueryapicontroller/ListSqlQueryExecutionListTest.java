package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.dtos.SqlQueryExecutionListFilterDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.UUID;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ListSqlQueryExecutionListTest extends AbstractPostLoginApiTest {

    private SqlQueryModel sqlQueryModel0;
    private SqlQueryModel sqlQueryModel1;

    @Before
    public void before() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryModel1 = sqlQueryModel(datasource);

        sqlQueryDbSetUp(ImmutableList.of(sqlQueryModel0, sqlQueryModel1));

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_ERROR, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, sqlQueryModel0.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, "result", SUCCESS, sqlQueryModel1.getId()) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(11, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel0.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(12, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, sqlQueryModel1.getId()) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(21, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, sqlQueryModel0.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(22, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, sqlQueryModel1.getId()) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(31, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, sqlQueryModel0.getId()) //Sun Oct 02 20:02:05 IST 2016
                                .values(32, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, sqlQueryModel1.getId()) //Sun Oct 02 20:02:05 IST 2016
                                .build()
                )
        );
        dbSetup.launch();
    }

    @Test
    public void testExecutionEndDateFilter() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "listSqlQueryExecution",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel0.getId()
                )
        );

        SqlQueryExecutionListFilterDto filter = new SqlQueryExecutionListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(2);
        //Sat Oct 01 20:21:47
        filter.setExecutionEndStart("01/10/2016 20:20");
        filter.setExecutionEndEnd("01/10/2016 20:22");

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is(sqlQueryModel0.getQuery())));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(1));
        assertThat(json, hasJsonPath("$.totalCount", is(1)));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionStartTime", is("Sat Oct 01 2016 19:51")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionEndTime", is("Sat Oct 01 2016 20:21")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].status", is(KILLED.name())));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].result", nullValue()));
    }

    @Test
    public void testExecutionStartDateFilter() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "listSqlQueryExecution",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel0.getId()
                )
        );

        SqlQueryExecutionListFilterDto filter = new SqlQueryExecutionListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(2);
        filter.setExecutionStartStart("29/09/2016 19:48");
        filter.setExecutionStartEnd("29/09/2016 19:50");

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is(sqlQueryModel0.getQuery())));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(1));
        assertThat(json, hasJsonPath("$.totalCount", is(1)));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionStartTime", is("Thu Sep 29 2016 19:49")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionEndTime", is("Thu Sep 29 2016 20:09")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].status", is(SUCCESS.name())));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].result", is("result")));
    }

    @Test
    public void testStatusFilter() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "listSqlQueryExecution",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel0.getId()
                )
        );

        SqlQueryExecutionListFilterDto filter = new SqlQueryExecutionListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(2);
        filter.setStatuses(ImmutableList.of(SUCCESS.name(), FAILURE.name()));

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, hasJsonPath("$.totalCount", is(2)));

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is(sqlQueryModel0.getQuery())));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(2));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionStartTime", is("Thu Sep 29 2016 19:49")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].sqlQueryExecutionStartTime", is("Fri Sep 30 2016 19:51")));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionEndTime", is("Thu Sep 29 2016 20:09")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].sqlQueryExecutionEndTime", is("Fri Sep 30 2016 20:11")));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].status", is(SUCCESS.name())));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].status", is(FAILURE.name())));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].result", is("result")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].result", nullValue()));
    }

    @Test
    public void testPaginationAndNonDefaultValues() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "listSqlQueryExecution",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel0.getId()
                )
        );

        SqlQueryExecutionListFilterDto filter = new SqlQueryExecutionListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(2);

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is(sqlQueryModel0.getQuery())));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(2));
        assertThat(json, hasJsonPath("$.totalCount", is(4)));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionStartTime", is("Thu Sep 29 2016 19:49")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].sqlQueryExecutionStartTime", is("Fri Sep 30 2016 19:51")));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionEndTime", is("Thu Sep 29 2016 20:09")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].sqlQueryExecutionEndTime", is("Fri Sep 30 2016 20:11")));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].status", is(SUCCESS.name())));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].status", is(FAILURE.name())));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].result", is("result")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].result", nullValue()));

        //Paginate
        filter.setPageNumber(1);
        //Set non null values
        filter.setExecutionStartStart("");
        filter.setExecutionStartEnd("");
        filter.setExecutionEndStart("");
        filter.setExecutionEndEnd("");
        filter.setStatuses(new LinkedList<>());

        jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is(sqlQueryModel0.getQuery())));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(2));
        assertThat(json, hasJsonPath("$.totalCount", is(4)));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionStartTime", is("Sat Oct 01 2016 19:51")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].sqlQueryExecutionStartTime", is("Sun Oct 02 2016 20:02")));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].sqlQueryExecutionEndTime", is("Sat Oct 01 2016 20:21")));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].sqlQueryExecutionEndTime", nullValue()));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].status", is(KILLED.name())));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].status", is(ONGOING.name())));

        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[0].result", nullValue()));
        assertThat(json, hasJsonPath("$.sqlQueryExecutionDtos[1].result", nullValue()));
    }
}


