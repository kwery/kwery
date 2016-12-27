package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
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

public class SqlQueryApiControllerListSqlQueriesTest extends AbstractPostLoginApiTest {

    private Datasource datasource;
    private SqlQueryModel sqlQueryModel0;
    private SqlQueryModel sqlQueryModel1;

    @Before
    public void setUpListSqlQueriesTest() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel0);

        sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel1);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(SqlQueryApiController.class, "listSqlQueries");

        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(url));
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);

        assertThat(json, isJson());
        assertThat(JsonPath.read(json, "$.length()"), is(2));

        assertThat(json, hasJsonPath("$[0].id", is(sqlQueryModel0.getId())));
        assertThat(json, hasJsonPath("$[1].id", is(sqlQueryModel1.getId())));

        assertThat(json, hasJsonPath("$[0].label", is(sqlQueryModel0.getLabel())));
        assertThat(json, hasJsonPath("$[1].label", is(sqlQueryModel1.getLabel())));

        assertThat(json, hasJsonPath("$[0].datasource.label", is(datasource.getLabel())));
        assertThat(json, hasJsonPath("$[1].datasource.label", is(datasource.getLabel())));
    }
}
