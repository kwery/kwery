package controllers.apis.integration.onboardingapicontroller;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.OnboardingApiController;
import controllers.apis.integration.AbstractApiTest;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static controllers.apis.OnboardingApiController.ROOT_USERNAME;
import static dtos.OnboardingNextActionDto.Action.ADD_DATASOURCE;
import static models.User.COLUMN_ID;
import static models.User.COLUMN_PASSWORD;
import static models.User.COLUMN_USERNAME;
import static models.User.TABLE_DASH_REPO_USER;
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
