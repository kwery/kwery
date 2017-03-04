package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.UserApiController;
import com.kwery.models.User;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.USER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.Messages.USER_DELETE_YOURSELF_M;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserApiControllerDeleteUserTest extends AbstractPostLoginApiTest {
    private User user;

    @Before
    public void setUpUserApiControllerDeleteUserTest() {
        user = TestUtil.user();
        userDbSetUp(user);
    }

    @Test
    public void testSuccess() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                UserApiController.class,
                "delete",
                ImmutableMap.of(
                        "userId", user.getId()
                )
        );

        String response = ninjaTestBrowser.postJson(getUrl(url), ImmutableMap.of());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(format(USER_DELETE_SUCCESS_M, user.getEmail()))));
    }

    @Test
    public void testDeleteYourself() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                UserApiController.class,
                "delete",
                ImmutableMap.of(
                        "userId", loggedInUser.getId()
                )
        );

        String response = ninjaTestBrowser.postJson(getUrl(url), ImmutableMap.of());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(format(USER_DELETE_YOURSELF_M))));
    }
}
