package controllers.apis.integration.onboardingapicontroller;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.OnboardingApiController;
import fluentlenium.utils.DbUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static dtos.OnboardingNextActionDto.Action.SHOW_RUNNING_QUERIES;
import static models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static models.SqlQuery.COLUMN_ID;
import static models.SqlQuery.COLUMN_LABEL;
import static models.SqlQuery.COLUMN_QUERY;
import static models.SqlQuery.TABLE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionShowRunningQueriesTest extends OnboardingApiControllerNextActionAddSqlQueryTest {
    @Before
    public void setUpOnboardingApiControllerNextActionShowExecutionQueriesTest () {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(TABLE)
                        .columns(COLUMN_ID, COLUMN_DATASOURCE_ID_FK, COLUMN_QUERY, COLUMN_CRON_EXPRESSION, COLUMN_LABEL)
                        .values("1", "1", "select * from foo", "*", "label")
                        .build()
        ).launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(SHOW_RUNNING_QUERIES.name())));
    }
}
