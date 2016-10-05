package controllers.apis.integration.sqlqueryapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.SqlQueryApiController;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dtos.SqlQueryExecutionListFilterDto;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.UUID;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static models.SqlQuery.COLUMN_QUERY;
import static models.SqlQueryExecution.COLUMN_EXECUTION_END;
import static models.SqlQueryExecution.COLUMN_EXECUTION_ID;
import static models.SqlQueryExecution.COLUMN_EXECUTION_START;
import static models.SqlQueryExecution.COLUMN_QUERY_RUN_ID_FK;
import static models.SqlQueryExecution.COLUMN_RESULT;
import static models.SqlQueryExecution.COLUMN_STATUS;
import static models.SqlQueryExecution.Status.FAILURE;
import static models.SqlQueryExecution.Status.KILLED;
import static models.SqlQueryExecution.Status.ONGOING;
import static models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ListSqlQueryExecutionTest extends AbstractPostLoginApiTest {
    @Before
    public void before() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select * from foo", 1).build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(2, "* * * * *", "testQuery1", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecution.TABLE)
                                .columns(SqlQueryExecution.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, 1475159940797l, "executionId", 1475158740747l, "result", SUCCESS, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(2, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, "result", SUCCESS, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
/*                                .values(3, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(4, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, FAILURE, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(5, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, 1) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(6, 1475159940797l, UUID.randomUUID().toString(), 1475158740747l, null, KILLED, 2) //Thu Sep 29 19:49:00 IST 2016  - Thu Sep 29 20:09:00 IST 2016
                                .values(7, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, 1)
                                .values(8, null, UUID.randomUUID().toString(), 1475158740747l, null, ONGOING, 2)*/


/*                                .values(9, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(10, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, "result", SUCCESS, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016*/
                                .values(11, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(12, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, FAILURE, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
/*                                .values(13, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, 1) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(14, 1475246507724l, UUID.randomUUID().toString(), 1475245307680l, null, KILLED, 2) //Fri Sep 30 19:51:47 IST 2016  - Fri Sep 30 20:11:47 IST 2016
                                .values(15, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, 1) //Fri Sep 30 19:51:47 IST 2016
                                .values(16, null, UUID.randomUUID().toString(), 1475245307680l, null, ONGOING, 2) //Fri Sep 30 19:51:47 IST 2016*/

/*                                .values(17, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, "result", SUCCESS, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(18, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, "result", SUCCESS, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(19, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(20, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, FAILURE, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016*/
                                .values(21, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, 1) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
                                .values(22, 1475333507680l, UUID.randomUUID().toString(), 1475331707680l, null, KILLED, 2) //Sat Oct 01 19:51:47 IST 2016 - Sat Oct 01 20:21:47 IST 2016
/*                                .values(23, null, UUID.randomUUID().toString(), 1475331707680l, null, ONGOING, 1) //Sat Oct 01 19:51:47 IST 2016
                                .values(24, null, UUID.randomUUID().toString(), 1475331707680l, null, ONGOING, 2) //Sat Oct 01 19:51:47 IST 2016*/

/*                                .values(25, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, "result", SUCCESS, 1) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(26, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, "result", SUCCESS, 2) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(27, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, FAILURE, 1) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(28, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, FAILURE, 2) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(29, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, KILLED, 1) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016
                                .values(30, 1475419925130l, UUID.randomUUID().toString(), 1475418725084l, null, KILLED, 2) //Sun Oct 02 20:02:05 IST 2016 - Sun Oct 02 20:22:05 IST 2016*/
                                .values(31, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, 1) //Sun Oct 02 20:02:05 IST 2016
                                .values(32, null, UUID.randomUUID().toString(), 1475418725084l, null, ONGOING, 2) //Sun Oct 02 20:02:05 IST 2016
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
                        "sqlQueryId", 1
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
        assertThat(json, hasJsonPath("$.sqlQuery", is("select * from foo")));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(1));

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
                        "sqlQueryId", 1
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
        assertThat(json, hasJsonPath("$.sqlQuery", is("select * from foo")));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(1));

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
                        "sqlQueryId", 1
                )
        );

        SqlQueryExecutionListFilterDto filter = new SqlQueryExecutionListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(2);
        filter.setStatuses(ImmutableList.of(SUCCESS.name(), FAILURE.name()));

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is("select * from foo")));
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
                        "sqlQueryId", 1
                )
        );

        SqlQueryExecutionListFilterDto filter = new SqlQueryExecutionListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(2);

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), filter);
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(json, hasJsonPath("$.sqlQuery", is("select * from foo")));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(2));

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
        assertThat(json, hasJsonPath("$.sqlQuery", is("select * from foo")));
        assertThat(JsonPath.read(json, "$.sqlQueryExecutionDtos.length()"), is(2));

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


