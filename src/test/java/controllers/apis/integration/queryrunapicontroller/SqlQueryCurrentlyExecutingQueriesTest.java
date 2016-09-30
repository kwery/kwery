package controllers.apis.integration.queryrunapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import conf.Routes;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
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
import static models.SqlQueryExecution.Status.ONGOING;
import static models.SqlQueryExecution.Status.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SqlQueryCurrentlyExecutingQueriesTest extends AbstractPostLoginApiTest {
    protected Datasource datasource;
    protected SqlQuery sqlQuery;

    @Before
    public void setUpQueryRunCurrentlyExecutingQueriesTest() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery", "select * from foo", 1).build(),
                        insertInto(SqlQueryExecution.TABLE)
                                .columns(SqlQueryExecution.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK)
                                .values(1, null, "sjfljkl", 1475215495171l, "status", SUCCESS, 1)
                                .values(2, null, "sjfljkl", 1475215495171l, null, ONGOING, 1)
                                .values(3, null, "sdjfklj", 1475215333445l, null, ONGOING, 1).build()
                )
        );
        dbSetup.launch();
    }

    @Test
    public void test() throws InterruptedException, IOException {
        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(Routes.CURRENTLY_EXECUTING_SQL_QUERY_API));

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, isJson());
        assertThat(JsonPath.read(json, "$.length()"), is(2));

        assertThat(json, hasJsonPath("$[0].sqlQueryLabel", is("testQuery")));
        assertThat(json, hasJsonPath("$[1].sqlQueryLabel", is("testQuery")));

        assertThat(json, hasJsonPath("$[0].sqlQueryExecutionStartTime", is("Fri Sep 30 2016 11:32")));
        assertThat(json, hasJsonPath("$[1].sqlQueryExecutionStartTime", is("Fri Sep 30 2016 11:34")));

        assertThat(json, hasJsonPath("$[0].datasourceLabel", is("testDatasource")));
        assertThat(json, hasJsonPath("$[1].datasourceLabel", is("testDatasource")));
    }
}
