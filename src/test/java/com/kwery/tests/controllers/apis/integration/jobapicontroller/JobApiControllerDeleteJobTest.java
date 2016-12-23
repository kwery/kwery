package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerDeleteJobTest extends AbstractPostLoginApiTest {
    protected JobModel jobModel;

    @Before
    public void setUpJobApiControllerDeleteJobTest() {
        jobModel = TestUtil.jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class,
                "deleteJob",
                ImmutableMap.of("jobId", jobModel.getId())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
    }
}
