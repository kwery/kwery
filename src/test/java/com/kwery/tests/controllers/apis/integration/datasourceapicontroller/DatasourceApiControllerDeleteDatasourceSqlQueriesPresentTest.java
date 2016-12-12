package com.kwery.tests.controllers.apis.integration.datasourceapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.DatasourceApiController;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.Messages.DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.kwery.views.ActionResult.Status.failure;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceApiControllerDeleteDatasourceSqlQueriesPresentTest extends AbstractPostLoginApiTest {
    Datasource datasource;

    @Before
    public void setUpDatasourceApiControllerDeleteDatasourceSqlQueriesPresentTest() throws DataSetException {
        datasource = datasource();

        datasourceDbSetup(datasource);

        SqlQueryModel m = sqlQueryModel();
        m.setDatasource(datasource);

        sqlQueryDbSetUp(m);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                DatasourceApiController.class,
                "delete",
                ImmutableMap.of(
                        "datasourceId", datasource.getId()
                )
        );

        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages.length()", is(1)));
        assertThat(response, hasJsonPath("$.messages[0]", is(DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M)));
    }
}
