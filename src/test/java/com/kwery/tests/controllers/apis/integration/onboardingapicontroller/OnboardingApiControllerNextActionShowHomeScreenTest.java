package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.models.JobModel;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.dtos.OnboardingNextActionDto.Action.SHOW_HOME_SCREEN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionShowHomeScreenTest extends OnboardingApiControllerNextActionAddSqlQueryTest {
    @Before
    public void setUpOnboardingApiControllerNextActionShowExecutionQueriesTest () {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(SHOW_HOME_SCREEN.name())));
    }
}
