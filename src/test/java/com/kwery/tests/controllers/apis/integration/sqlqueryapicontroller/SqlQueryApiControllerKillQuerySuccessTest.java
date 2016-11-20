package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import ninja.Router;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.scheduler.OngoingSqlQueryTask;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.MySqlDocker;
import com.kwery.views.ActionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.TestUtil.sleepSqlQuery;

public class SqlQueryApiControllerKillQuerySuccessTest extends AbstractPostLoginApiTest {
    protected MySqlDocker mySqlDocker = new MySqlDocker();

    protected SchedulerService schedulerService;
    protected SqlQuery sqlQuery;

    @Before
    public void setUpDownSqlQueryKillQueryTest() {
        mySqlDocker.start();

        Datasource datasource = mySqlDocker.datasource();
        getInjector().getInstance(DatasourceDao.class).save(datasource);

        sqlQuery = sleepSqlQuery(datasource);
        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = getInjector().getInstance(SchedulerService.class);

        schedulerService.schedule(sqlQuery);
    }

    @Test
    public void test() throws InterruptedException, JsonProcessingException {
        TimeUnit.SECONDS.sleep(70);

        List<OngoingSqlQueryTask> ongoingSqlQueryTasks = schedulerService.ongoingQueryTasks(sqlQuery.getId());

        assertThat(ongoingSqlQueryTasks.size(), greaterThanOrEqualTo(1));

        OngoingSqlQueryTask ongoingSqlQueryTask = schedulerService.ongoingQueryTasks(sqlQuery.getId()).get(0);

        SqlQueryApiController.SqlQueryExecutionIdContainer sqlQueryExecutionIdContainer = new SqlQueryApiController.SqlQueryExecutionIdContainer();
        sqlQueryExecutionIdContainer.setSqlQueryExecutionId(ongoingSqlQueryTask.getExecutionId());

        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "killSqlQuery",
                ImmutableMap.of(
                        "sqlQueryId", sqlQuery.getId()
                )
        );

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), sqlQueryExecutionIdContainer);

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, isJson());

        assertThat(json, hasJsonPath("$.status", is(ActionResult.Status.success.name())));
        assertThat(json, hasJsonPath("$.messages", is(empty())));

        ongoingSqlQueryTasks = schedulerService.ongoingQueryTasks(sqlQuery.getId());

        List<String> executionIds = new ArrayList<>(ongoingSqlQueryTasks.size());

        for (OngoingSqlQueryTask ongoing : ongoingSqlQueryTasks) {
            executionIds.add(ongoing.getExecutionId());
        }

        assertThat(executionIds, not(hasItem(ongoingSqlQueryTask.getExecutionId())));
    }

    @After
    public void tearDownSqlQueryKillQueryTest() {
        mySqlDocker.tearDown();
    }
}
