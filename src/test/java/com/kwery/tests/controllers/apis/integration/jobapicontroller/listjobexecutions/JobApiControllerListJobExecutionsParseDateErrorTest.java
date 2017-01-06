package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobexecutions;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionListFilterDto;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Random;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR_M;
import static com.kwery.views.ActionResult.Status.failure;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobExecutionsParseDateErrorTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        JobExecutionListFilterDto filter = new JobExecutionListFilterDto();

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobExecutions",
                ImmutableMap.of("jobId", new Random().nextInt()));

        //First page
        filter.setResultCount(2);
        filter.setPageNumber(0);
        filter.setExecutionStartStart("start");
        filter.setExecutionStartEnd("end");

        String response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(MessageFormat.format(JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR_M, "start"))));
        assertThat(response, hasJsonPath("$.messages[1]", is(MessageFormat.format(JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR_M, "end"))));
    }
}
