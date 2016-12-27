package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerStopJobExecutionSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected String executionId;

    @Before
    public void setUpJobApiControllerStopJobTest() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setQuery("select sleep(1000000)");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        getInjector().getInstance(JobService.class).launch(jobModel.getId());

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobModel.getId());

        JobExecutionDao jobExecutionDao = getInjector().getInstance(JobExecutionDao.class);

        waitAtMost(TIMEOUT_SECONDS, SECONDS).until(() -> !jobExecutionDao.filter(filter).isEmpty());

        executionId = jobExecutionDao.filter(filter).get(0).getExecutionId();
    }

    @Test
    public void testSuccess() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class, "stopJobExecution", ImmutableMap.of("jobExecutionId", executionId)
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
    }
}
