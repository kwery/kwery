package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.DatasourceDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.ADD_SQL_QUERY_API;
import static com.kwery.tests.util.Messages.QUERY_RUN_WITH_CRON_ADDITION_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.queryRunDto;

public class SqlQueryApiControllerAddSqlQueryWithCronExpressionSuccessTest extends AbstractPostLoginApiTest {
    protected Datasource datasource;

    @Before
    public void setUpAddQueryRunSuccessTest() {
        datasource = datasource();
        getInjector().getInstance(DatasourceDao.class).save(datasource);
    }

    @Test
    public void test() throws IOException {
        SqlQueryDto dto = queryRunDto();
        dto.setDatasourceId(datasource.getId());
        assertSuccess(
                actionResult(ninjaTestBrowser.postJson(getUrl(ADD_SQL_QUERY_API), dto)),
                QUERY_RUN_WITH_CRON_ADDITION_SUCCESS_M
        );
    }
}
