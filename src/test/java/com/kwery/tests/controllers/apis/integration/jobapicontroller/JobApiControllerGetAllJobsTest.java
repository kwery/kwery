package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import ninja.Router;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.toJson;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JobApiControllerGetAllJobsTest extends AbstractPostLoginApiTest {
    JobModel jobModel;
    JobModel dependentJobModel;

    @Before
    public void setUpJobApiControllerGetAllJobsTest() {
        jobModel = jobModelWithoutDependents();
        dependentJobModel = jobModelWithoutDependents();

        jobDbSetUp(ImmutableList.of(jobModel, dependentJobModel));

        jobModel.getChildJobs().add(dependentJobModel);

        DbUtil.jobDependentDbSetUp(jobModel);

        dependentJobModel.setParentJob(jobModel);
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listAllJobs");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        assertThat(response, isJson());
        assertEquals(toJson(ImmutableList.of(new JobModelHackDto(jobModel), new JobModelHackDto(dependentJobModel, jobModel))), response, true);
    }
}
