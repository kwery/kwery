package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobModelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.toJson;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JobApiControllerGetAllJobsTest extends AbstractPostLoginApiTest {
    List<JobModel> jobModels;

    @Before
    public void setUpJobApiControllerGetAllJobsTest() {
        jobModels = new ArrayList<>(2);

        for (int i = 0; i < 2; ++i) {
            JobModel jobModel = jobModelWithoutDependents();
            jobModel.setSqlQueries(new HashSet<>());
            jobModels.add(jobModel);
        }

        jobModelDbSetUp(jobModels);
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listAllJobs");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        assertThat(response, isJson());
        assertEquals(toJson(jobModels), response, true);
    }
}
