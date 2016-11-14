package controllers.apis.integration.onboardingapicontroller;

import controllers.apis.OnboardingApiController;
import controllers.apis.integration.AbstractApiTest;
import ninja.Router;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.HashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static controllers.apis.OnboardingApiController.ROOT_USERNAME;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static util.Messages.ONBOARDING_ROOT_USER_CREATED_HTML_M;
import static util.Messages.ONBOARDING_ROOT_USER_CREATED_M;
import static views.ActionResult.Status.success;

public class OnboardingApiControllerAddRootUserTest extends AbstractApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "addRootUser");
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());

        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(MessageFormat.format(ONBOARDING_ROOT_USER_CREATED_HTML_M, ROOT_USERNAME, "foobarmoo"))));
    }
}
