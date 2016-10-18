package controllers.apis.integration.onboardingapicontroller;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.OnboardingApiController;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static dtos.OnboardingNextActionDto.Action.ADD_SQL_QUERY;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionAddSqlQueryTest extends OnboardingApiControllerNextActionAddDatasourceTest {
    @Before
    public void setUpOnboardingApiControllerNextActionAddSqlQueryTest () {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(Datasource.TABLE)
                .columns(Datasource.COLUMN_ID, Datasource.COLUMN_USERNAME, Datasource.COLUMN_PASSWORD, Datasource.COLUMN_URL, Datasource.COLUMN_PORT, Datasource.COLUMN_LABEL, Datasource.COLUMN_TYPE)
                .values("1", "user", "password", "foo.com", "3306", "label", MYSQL.name())
                .build()
        ).launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(ADD_SQL_QUERY.name())));
    }
}
