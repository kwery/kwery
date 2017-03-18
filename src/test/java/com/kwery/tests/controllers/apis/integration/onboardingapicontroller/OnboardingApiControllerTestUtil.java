package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.dtos.OnboardingNextActionDto;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerTestUtil {
    public static void assertNextAction(String response, OnboardingNextActionDto.Action action) {
        assertThat(response, isJson(allOf(
                withJsonPath("$.action", is(action.name()))
        )));
    }
}
