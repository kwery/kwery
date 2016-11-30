package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.conf.Routes;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecutionModel.Status.ONGOING;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.models.SqlQueryModel.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerListExecutingQueriesTest extends AbstractPostLoginApiTest {
    @Before
    public void setUpQueryRunCurrentlyExecutingQueriesTest() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "* * * * *", "testQuery", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, null, "sjfljkl", 1475215495171l, "status", SUCCESS, 1)
                                .values(2, null, "executionId1", 1475215495171l, null, ONGOING, 1)
                                .values(3, null, "executionId0", 1475215333445l, null, ONGOING, 1).build()
                )
        );
        dbSetup.launch();
    }

    @Test
    public void test() throws InterruptedException, IOException {
        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(Routes.EXECUTING_SQL_QUERY_API));

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, isJson());
        assertThat(JsonPath.read(json, "$.length()"), is(2));

        assertThat(json, hasJsonPath("$[0].sqlQueryLabel", is("testQuery")));
        assertThat(json, hasJsonPath("$[1].sqlQueryLabel", is("testQuery")));

        assertThat(json, hasJsonPath("$[0].sqlQueryExecutionStartTime", is("Fri Sep 30 2016 11:32")));
        assertThat(json, hasJsonPath("$[1].sqlQueryExecutionStartTime", is("Fri Sep 30 2016 11:34")));

        assertThat(json, hasJsonPath("$[0].datasourceLabel", is("testDatasource")));
        assertThat(json, hasJsonPath("$[1].datasourceLabel", is("testDatasource")));

        assertThat(json, hasJsonPath("$[0].sqlQueryId", is(1)));
        assertThat(json, hasJsonPath("$[1].sqlQueryId", is(1)));

        assertThat(json, hasJsonPath("$[0].sqlQueryExecutionId", is("executionId0")));
        assertThat(json, hasJsonPath("$[1].sqlQueryExecutionId", is("executionId1")));
    }
}
