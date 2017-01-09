package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerDeleteDependentJobTest extends AbstractPostLoginApiTest {
    protected JobModel parentJobModel;
    private JobModel childJobModel;
    private JobDao jobDao;

    @Before
    public void setUpJobApiControllerDeleteChildJobTest() {
        parentJobModel = jobModelWithoutDependents();
        parentJobModel.setSqlQueries(new HashSet<>());
        parentJobModel.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel);

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel parentSqlQueryModel = sqlQueryModel();
        parentSqlQueryModel.setDatasource(datasource);
        parentSqlQueryModel.setQuery("select User from mysql.user where User = 'root'");

        sqlQueryDbSetUp(parentSqlQueryModel);

        parentJobModel.getSqlQueries().add(parentSqlQueryModel);
        jobSqlQueryDbSetUp(parentJobModel);

        childJobModel = jobModelWithoutDependents();
        childJobModel.setCronExpression(null);
        childJobModel.setSqlQueries(new HashSet<>());
        parentJobModel.getChildJobs().add(childJobModel);

        jobDbSetUp(childJobModel);
        jobDependentDbSetUp(parentJobModel);

        SqlQueryModel childSqlQueryModel = sqlQueryModel();
        childSqlQueryModel.setDatasource(datasource);
        childSqlQueryModel.setQuery("select User from mysql.user where User = 'root'");

        sqlQueryDbSetUp(childSqlQueryModel);

        childJobModel.getSqlQueries().add(childSqlQueryModel);
        jobSqlQueryDbSetUp(childJobModel);

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void testDeleteChildJob() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class,
                "deleteJob",
                ImmutableMap.of("jobId", childJobModel.getId())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
    }

    @Test
    public void testDeleteParentJob() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class,
                "deleteJob",
                ImmutableMap.of("jobId", parentJobModel.getId())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M)));

        assertThat(jobDao.getAllJobs(), hasSize(2));
    }
}
