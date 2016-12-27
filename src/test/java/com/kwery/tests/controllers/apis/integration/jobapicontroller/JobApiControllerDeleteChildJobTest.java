package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerDeleteChildJobTest extends AbstractPostLoginApiTest {
    protected JobModel parentJobModel;
    private JobModel childJobModel;

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
        parentJobModel.getDependentJobs().add(childJobModel);

        jobDbSetUp(childJobModel);
        jobDependentDbSetUp(parentJobModel);

        SqlQueryModel childSqlQueryModel = sqlQueryModel();
        childSqlQueryModel.setDatasource(datasource);
        childSqlQueryModel.setQuery("select User from mysql.user where User = 'root'");

        sqlQueryDbSetUp(childSqlQueryModel);

        childJobModel.getSqlQueries().add(childSqlQueryModel);
        jobSqlQueryDbSetUp(childJobModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class,
                "deleteJob",
                ImmutableMap.of("jobId", childJobModel.getId())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
    }
}
