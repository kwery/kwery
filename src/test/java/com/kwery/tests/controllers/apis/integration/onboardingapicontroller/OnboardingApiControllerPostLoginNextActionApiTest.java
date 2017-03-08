package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.dtos.OnboardingNextActionDto.Action;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.dtos.OnboardingNextActionDto.Action.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

@RunWith(Parameterized.class)
public class OnboardingApiControllerPostLoginNextActionApiTest extends AbstractPostLoginApiTest {
    static {
        System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
    }

    protected Action action;

    public OnboardingApiControllerPostLoginNextActionApiTest(Action action) {
        this.action = action;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {ADD_DATASOURCE},
                {ADD_JOB},
                {SHOW_HOME_SCREEN}
        });
    }

    @Before
    public void setUp() {
        if (action == SHOW_HOME_SCREEN) {
            jobDbSetUp(jobModelWithoutDependents());
        }

        if (action == ADD_JOB || action == SHOW_HOME_SCREEN) {
            datasourceDbSetup(datasource());
        }
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        OnboardingApiControllerTestUtil.assertNextAction(response, action);
    }
}
