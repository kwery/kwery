package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.views.ActionResult.Status.failure;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerStopJobExecutionFailureTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class, "stopJobExecution", ImmutableMap.of("jobExecutionId", UUID.randomUUID().toString())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
    }
}
