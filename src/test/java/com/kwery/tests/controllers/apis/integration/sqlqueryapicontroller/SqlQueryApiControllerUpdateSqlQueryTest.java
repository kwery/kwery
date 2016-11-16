package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import ninja.Router;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.MySqlDocker;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.text.MessageFormat.format;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.QUERY_RUN_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.QUERY_RUN_UPDATE_SUCCESS_M;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;

public class SqlQueryApiControllerUpdateSqlQueryTest extends AbstractPostLoginApiTest {
    protected MySqlDocker mySqlDocker;
    protected Datasource datasource;

    @Before
    public void setUpSqlQueryApiControllerUpdateSqlQueryTest() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();

        datasource = mySqlDocker.datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .values(2, "testDatasource1", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "testQuery0", "select sleep(86400)", 1)
                                .values(2, "* * * * *", "testQuery1", "select sleep(86400)", 1)
                                .build()
                )
        ).launch();

        getInjector().getInstance(SchedulerService.class).schedule(getInjector().getInstance(SqlQueryDao.class).getById(1));
    }

    @Test
    public void testUpdateSuccess() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(SqlQueryApiController.class, "addSqlQuery");

        SqlQueryDto dto = new SqlQueryDto();
        dto.setId(1);
        dto.setCronExpression("5 * * * *");
        dto.setLabel("foo");
        dto.setQuery("select * from mysql.db");
        dto.setDatasourceId(2);

        String response = ninjaTestBrowser.postJson(getUrl(url), dto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(QUERY_RUN_UPDATE_SUCCESS_M)));
    }

    @Test
    public void testUpdateWithConflictingLabel() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(SqlQueryApiController.class, "addSqlQuery");

        SqlQueryDto dto = new SqlQueryDto();
        dto.setId(1);
        dto.setCronExpression("5 * * * *");
        dto.setLabel("testQuery1");
        dto.setQuery("select * from mysql.db");
        dto.setDatasourceId(2);

        String response = ninjaTestBrowser.postJson(getUrl(url), dto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(format(QUERY_RUN_ADDITION_FAILURE_M, "testQuery1"))));
    }

    @After
    public void tearDownSqlQueryApiControllerUpdateSqlQueryTest() {
        mySqlDocker.tearDown();
    }
}
