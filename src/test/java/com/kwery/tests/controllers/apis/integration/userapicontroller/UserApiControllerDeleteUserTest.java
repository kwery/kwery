package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.google.common.collect.ImmutableMap;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.controllers.apis.UserApiController;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static java.text.MessageFormat.format;
import static com.kwery.models.User.TABLE_DASH_REPO_USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.USER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.Messages.USER_DELETE_YOURSELF_M;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;

public class UserApiControllerDeleteUserTest extends AbstractPostLoginApiTest {
    @Before
    public void setUpUserApiControllerDeleteUserTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(TABLE_DASH_REPO_USER)
                .columns("id", "username", "password")
                .values(2, "foo", "bar")
                .build()
        ).launch();
    }

    @Test
    public void testSuccess() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                UserApiController.class,
                "delete",
                ImmutableMap.of(
                        "userId", 2
                )
        );

        String response = ninjaTestBrowser.postJson(getUrl(url), ImmutableMap.of());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(format(USER_DELETE_SUCCESS_M, "foo"))));
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
