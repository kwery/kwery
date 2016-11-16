package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.kwery.controllers.apis.OnboardingApiController.ROOT_USERNAME;
import static com.kwery.dtos.OnboardingNextActionDto.Action.ADD_DATASOURCE;
import static com.kwery.models.User.COLUMN_ID;
import static com.kwery.models.User.COLUMN_PASSWORD;
import static com.kwery.models.User.COLUMN_USERNAME;
import static com.kwery.models.User.TABLE_DASH_REPO_USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionAddDatasourceTest extends AbstractApiTest {
    @Before
    public void setUpOnboardingApiControllerNextActionAddDatasourceTest() {
        UserTableUtil userTableUtil = new UserTableUtil();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                insertInto(TABLE_DASH_REPO_USER)
                        .columns(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD)
                        .values(1, ROOT_USERNAME, "foobarmoo")
                        .build()
        ).launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(ADD_DATASOURCE.name())));
    }
}
