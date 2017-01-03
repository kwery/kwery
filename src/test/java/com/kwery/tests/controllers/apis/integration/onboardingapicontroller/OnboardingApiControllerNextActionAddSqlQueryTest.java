package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.models.Datasource;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.dtos.OnboardingNextActionDto.Action.ADD_JOB;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.datasource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionAddSqlQueryTest extends OnboardingApiControllerNextActionAddDatasourceTest {
    @Before
    public void setUpOnboardingApiControllerNextActionAddSqlQueryTest () {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(ADD_JOB.name())));
    }
}
