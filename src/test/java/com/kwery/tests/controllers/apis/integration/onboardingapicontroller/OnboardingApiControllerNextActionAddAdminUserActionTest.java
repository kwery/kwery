package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import ninja.Router;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.dtos.OnboardingNextActionDto.Action.ADD_ADMIN_USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionAddAdminUserActionTest extends AbstractApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(ADD_ADMIN_USER.name())));
    }
}
