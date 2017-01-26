package com.kwery.tests.controllers.apis.integration.jobapicontroller.executionresult;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Test;

import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_REPORT_NOT_FOUND_M;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.views.ActionResult.Status.failure;

public class JobApiControllerExecutionResultNotFoundTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "jobExecutionResult", ImmutableMap.of("jobExecutionId", "foo"));
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        assertJsonActionResult(response, new ActionResult(failure, JOBAPICONTROLLER_REPORT_NOT_FOUND_M));
    }
}
