package com.kwery.tests.controllers.apis.integration.datasourceapicontroller;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.controllers.apis.DatasourceApiController;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.TABLE;
import static com.kwery.models.Datasource.Type.MYSQL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceApiControllerTestGetDatasource extends AbstractPostLoginApiTest {
    @Before
    public void setUpDatasourceApiControllerTestGetDatasource() {
        DbSetup dbSetup = new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                insertInto(TABLE)
                        .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                        .values(1, "testDatasource0", "password0", 3306, MYSQL.name(), "foo.com", "user0")
                        .build()
        );

        dbSetup.launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                DatasourceApiController.class,
                "datasource",
                ImmutableMap.of(
                    "datasourceId", 1
                )
        );

        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        Object object = Configuration.defaultConfiguration().jsonProvider().parse(response);

        assertThat(object, isJson());

        assertThat(object, hasJsonPath("$.id", is(1)));
        assertThat(object, hasJsonPath("$.label", is("testDatasource0")));
        assertThat(object, hasJsonPath("$.username", is("user0")));
        assertThat(object, hasJsonPath("$.password", is("password0")));
        assertThat(object, hasJsonPath("$.url", is("foo.com")));
        assertThat(object, hasJsonPath("$.port", is(3306)));
        assertThat(object, hasJsonPath("$.type", is(MYSQL.name())));
    }
}
