package controllers.apis.integration.queryrunapicontroller;

import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import dtos.SqlQueryDto;
import models.Datasource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static conf.Routes.ADD_SQL_QUERY_API;
import static util.Messages.QUERY_RUN_ADDITION_SUCCESS_M;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRunDto;

public class AddSqlQuerySuccessTest extends AbstractPostLoginApiTest {
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
                QUERY_RUN_ADDITION_SUCCESS_M
        );
    }
}