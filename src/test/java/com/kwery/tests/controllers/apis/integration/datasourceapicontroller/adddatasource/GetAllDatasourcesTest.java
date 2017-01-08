package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.jayway.jsonpath.matchers.JsonPathMatchers;
import com.kwery.controllers.apis.DatasourceApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GetAllDatasourcesTest extends AbstractPostLoginApiTest {
    protected Datasource saved0;
    protected Datasource saved1;

    @Before
    public void setUpGetAllDatasourcesTest() {
        DatasourceDao datasourceDao = getInjector().getInstance(DatasourceDao.class);

        saved0 = TestUtil.datasourceWithoutId();
        datasourceDao.save(saved0);

        saved1 = TestUtil.datasourceWithoutId();
        datasourceDao.save(saved1);
    }

    @Test
    public void test() throws IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(DatasourceApiController.class, "allDatasources");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, JsonPathMatchers.hasJsonPath("$.[*]", hasSize(2)));

        //Ensure password masking is working
        assertThat(response, JsonPathMatchers.hasJsonPath("$.[0].password", is("")));
        assertThat(response, JsonPathMatchers.hasJsonPath("$.[1].password", is("")));
    }
}
