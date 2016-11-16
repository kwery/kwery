package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.TestUtil;

import java.io.IOException;
import java.util.List;

import static com.kwery.conf.Routes.ALL_DATASOURCES_API;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class GetAllDatasourcesTest extends AbstractPostLoginApiTest {
    protected Datasource saved0;
    protected Datasource saved1;

    @Before
    public void setUpGetAllDatasourcesTest() {
        DatasourceDao datasourceDao = getInjector().getInstance(DatasourceDao.class);

        saved0 = TestUtil.datasource();
        datasourceDao.save(saved0);

        saved1 = TestUtil.datasource();
        saved1.setLabel("lsdjfklj");
        datasourceDao.save(saved1);
    }

    @Test
    public void test() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Datasource> datasources = mapper.readValue(
                ninjaTestBrowser.makeJsonRequest(getUrl(ALL_DATASOURCES_API)),
                mapper.getTypeFactory().constructCollectionType(List.class, Datasource.class)
        );

        assertThat(datasources, hasSize(2));
    }
}
