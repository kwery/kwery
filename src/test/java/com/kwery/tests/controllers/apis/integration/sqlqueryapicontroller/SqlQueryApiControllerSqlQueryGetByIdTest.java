package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerSqlQueryGetByIdTest extends AbstractPostLoginApiTest {

    private SqlQueryModel sqlQueryModel;
    private Datasource datasource;

    @Before
    public void SqlQueryApiControllerSqlQueryGetByIdTest () {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "sqlQuery",
                ImmutableMap.of(
                        "sqlQueryId", sqlQueryModel.getId()
                )
        );
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.id", is(sqlQueryModel.getId())));
        assertThat(response, hasJsonPath("$.query", is(sqlQueryModel.getQuery())));
        assertThat(response, hasJsonPath("$.label", is(sqlQueryModel.getLabel())));
        assertThat(response, hasJsonPath("$.datasource.id", is(datasource.getId())));
    }
}
