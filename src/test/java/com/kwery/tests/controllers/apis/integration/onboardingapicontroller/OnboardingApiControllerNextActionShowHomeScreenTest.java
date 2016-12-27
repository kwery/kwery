package com.kwery.tests.controllers.apis.integration.onboardingapicontroller;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.dtos.OnboardingNextActionDto.Action.SHOW_HOME_SCREEN;
import static com.kwery.models.SqlQueryModel.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingApiControllerNextActionShowHomeScreenTest extends OnboardingApiControllerNextActionAddSqlQueryTest {
    @Before
    public void setUpOnboardingApiControllerNextActionShowExecutionQueriesTest () {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(SQL_QUERY_TABLE)
                        .columns(ID_COLUMN, DATASOURCE_ID_FK_COLUMN, QUERY_COLUMN, LABEL_COLUMN)
                        .values("1", "1", "select * from foo", "label")
                        .build()
        ).launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(OnboardingApiController.class, "nextAction");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.action", is(SHOW_HOME_SCREEN.name())));
    }
}
