package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.dtos.OnboardingNextActionDto;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.dtos.OnboardingNextActionDto.Action.LOGIN;
import static com.kwery.dtos.OnboardingNextActionDto.Action.SIGN_UP;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;

@RunWith(Parameterized.class)
public class OnboardingApiControllerPreLoginNextActionApiTest extends AbstractApiTest {
    protected OnboardingNextActionDto.Action action;

    public OnboardingApiControllerPreLoginNextActionApiTest(OnboardingNextActionDto.Action action) {
        this.action = action;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {LOGIN},
                {SIGN_UP},
        });
    }

    @Before
    public void setUp() {
        if (action == LOGIN) {
            userDbSetUp(TestUtil.user());
        }
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        OnboardingApiControllerTestUtil.assertNextAction(response, action);
    }
}
