package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.UserApiController;
import com.kwery.models.User;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

//ToDO requires refactoring related to user login
public class UserApiControllerListAllApiTest extends AbstractPostSuperUserLoginApiTest {
    private User user0;
    private User user1;

    @Before
    public void setUpUserListAllApiTest() {
        user0 = TestUtil.user();
        userDbSetUp(user0);

        user1 = TestUtil.user();
        userDbSetUp(user1);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(UserApiController.class, "list");

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        List<User> users = Lists.newArrayList(user0, user1, loggedInUser);
        users.sort(Comparator.comparing(User::getId));

        assertUser(jsonResponse, 0, users.get(0));
        assertUser(jsonResponse, 1, users.get(1));
        assertUser(jsonResponse, 2, users.get(2));

        assertThat(JsonPath.read(jsonResponse, "$.length()"), is(3));
    }

    private void assertUser(String response, int index, User user) {
                assertThat(response, isJson(allOf(
                        withJsonPath(String.format("$.[%d].id", index), is(user.getId())),
                        withJsonPath(String.format("$.[%d].firstName", index), is(user.getFirstName())),
                        withJsonPath(String.format("$.[%d].middleName", index), is(user.getMiddleName())),
                        withJsonPath(String.format("$.[%d].lastName", index) , is(user.getLastName())),
                        withJsonPath(String.format("$.[%d].email", index), is(user.getEmail())),
                        withJsonPath(String.format("$.[%d].password", index), is(""))
        )));
    }
}
