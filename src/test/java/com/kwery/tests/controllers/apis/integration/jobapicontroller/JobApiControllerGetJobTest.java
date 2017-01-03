package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.util.TestUtil.toJson;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class JobApiControllerGetJobTest extends AbstractPostLoginApiTest {
    private JobModel jobModel;
    private JobModel dependentJob;

    @Before
    public void setUpJobApiControllerGetJobTest() {
        jobModel = TestUtil.jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);

        Datasource datasource = TestUtil.datasource();
        DbUtil.datasourceDbSetup(datasource);

        jobModel.getSqlQueries().addAll(ImmutableSet.of(TestUtil.sqlQueryModel(datasource)));
        DbUtil.sqlQueryDbSetUp(jobModel.getSqlQueries());
        DbUtil.jobSqlQueryDbSetUp(jobModel);

        jobModel.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        DbUtil.jobEmailDbSetUp(jobModel);

        dependentJob = TestUtil.jobModelWithoutDependents();
        DbUtil.jobDbSetUp(dependentJob);

        dependentJob.getSqlQueries().addAll(ImmutableSet.of(TestUtil.sqlQueryModel(datasource)));
        DbUtil.sqlQueryDbSetUp(dependentJob.getSqlQueries());
        DbUtil.jobSqlQueryDbSetUp(dependentJob);
        dependentJob.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        DbUtil.jobEmailDbSetUp(dependentJob);

        dependentJob.setParentJob(jobModel);

        jobModel.getChildJobs().add(dependentJob);

        DbUtil.jobDependentDbSetUp(jobModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class,
                "getJob",
                ImmutableMap.of("jobId", jobModel.getId())
        );

        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        String expected = toJson(new JobModelHackDto(jobModel));
        assertThat(response, sameJSONAs(expected));
    }

    @Test
    public void testDependentJob() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class,
                "getJob",
                ImmutableMap.of("jobId", dependentJob.getId())
        );

        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        String expected = toJson(new JobModelHackDto(dependentJob, jobModel));
        assertThat(response, sameJSONAs(expected));
    }
}
