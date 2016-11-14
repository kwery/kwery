package controllers.apis.integration.userapicontroller.getuser;

import com.google.common.collect.ImmutableMap;
import controllers.apis.UserApiController;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserApiControllerGetUserByIdTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                UserApiController.class,
                "userById",
                ImmutableMap.of(
                        "userId", loggedInUser.getId()
                )
        );

        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.id", is(loggedInUser.getId())));
        assertThat(response, hasJsonPath("$.username", is(loggedInUser.getUsername())));
        assertThat(response, hasJsonPath("$.password", is(loggedInUser.getPassword())));
    }
}

