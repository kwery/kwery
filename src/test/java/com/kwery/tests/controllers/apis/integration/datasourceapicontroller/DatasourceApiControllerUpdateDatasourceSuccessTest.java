package com.kwery.tests.controllers.apis.integration.datasourceapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.DatasourceApiController;
import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import java.text.MessageFormat;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.Messages.DATASOURCE_UPDATE_SUCCESS_M;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

//TODO - Validation test cases for update
public class DatasourceApiControllerUpdateDatasourceSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    MySQLContainer mysql = new MySQLContainer();

    protected Datasource datasource;

    @Before
    public void setUpDatasourceApiControllerUpdateDatasourceSuccessTest() {
        datasource = TestUtil.datasource(mysql, Datasource.Type.MYSQL);

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, datasource.getLabel(), datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        datasource.setId(1);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(DatasourceApiController.class, "addDatasource");

        datasource.setLabel("foo");

        String response = ninjaTestBrowser.postJson(
                getUrl(url),
                datasource
        );

        Object object = Configuration.defaultConfiguration().jsonProvider().parse(response);
        assertThat(JsonPath.read(response, "$.messages.length()"), is(1));
        assertThat(object, hasJsonPath("$.messages[0]", is(MessageFormat.format(DATASOURCE_UPDATE_SUCCESS_M, MYSQL.name(), datasource.getLabel()))));
    }
}
