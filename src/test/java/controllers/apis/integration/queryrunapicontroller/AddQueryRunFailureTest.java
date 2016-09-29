package controllers.apis.integration.queryrunapicontroller;

import conf.Routes;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import dao.QueryRunDao;
import dtos.QueryRunDto;
import models.Datasource;
import models.QueryRun;
import org.junit.Before;
import org.junit.Test;
import util.Messages;

import java.io.IOException;

import static java.text.MessageFormat.format;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRunDto;

public class AddQueryRunFailureTest extends AbstractPostLoginApiTest {
    protected Datasource addedDatasource;
    protected QueryRunDto dto;
    protected QueryRunDao dao;

    @Before
    public void setUpAddQueryRunFailureTest() {
        addedDatasource = datasource();
        getInjector().getInstance(DatasourceDao.class).save(addedDatasource);

        dao = getInjector().getInstance(QueryRunDao.class);
        dto = queryRunDto();
        dto.setDatasourceId(addedDatasource.getId());

        QueryRun queryRun = new QueryRun();
        queryRun.setCronExpression(dto.getCronExpression());
        queryRun.setQuery(dto.getQuery());
        queryRun.setLabel(dto.getLabel());
        queryRun.setDatasource(addedDatasource);

        getInjector().getInstance(QueryRunDao.class).save(queryRun);
    }

    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(Routes.ADD_QUERY_RUN_API), dto)),
                format(Messages.QUERY_RUN_ADDITION_FAILURE_M, dto.getLabel())
        );
    }
}
