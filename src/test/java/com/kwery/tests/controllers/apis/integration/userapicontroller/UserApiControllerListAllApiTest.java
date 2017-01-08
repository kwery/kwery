package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.UserApiController;
import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

//ToDO requires refactoring related to user login
public class UserApiControllerListAllApiTest extends AbstractApiTest {
    protected UserTableUtil userTableUtil;
    protected Router router;

    @Before
    public void setUpUserListAllApiTest() {
        userTableUtil = new UserTableUtil(2);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation()
                )
        );
        dbSetup.launch();

        router = getInjector().getInstance(Router.class);

        String loginUrl = router.getReverseRoute(UserApiController.class, "login");

        ninjaTestBrowser.postJson(getUrl(loginUrl), userTableUtil.firstRow());
    }

    @Test
    public void test() {
        String url = router.getReverseRoute(UserApiController.class, "list");

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());

        assertThat(JsonPath.read(jsonResponse, "$.length()"), is(2));

        User user0 = userTableUtil.row(0);

        assertThat(json, hasJsonPath("$[0].id", is(user0.getId())));
        assertThat(json, hasJsonPath("$[0].username", is(user0.getUsername())));
        assertThat(json, hasJsonPath("$[0].password", is("")));

        User user1 = userTableUtil.row(1);

        assertThat(json, hasJsonPath("$[1].id", is(user1.getId())));
        assertThat(json, hasJsonPath("$[1].username", is(user1.getUsername())));
        assertThat(json, hasJsonPath("$[1].password", is("")));
    }
}
