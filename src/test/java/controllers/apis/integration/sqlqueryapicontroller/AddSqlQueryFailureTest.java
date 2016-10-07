package controllers.apis.integration.sqlqueryapicontroller;

import conf.Routes;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dtos.SqlQueryDto;
import models.Datasource;
import models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import util.Messages;

import java.io.IOException;

import static java.text.MessageFormat.format;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRunDto;

public class AddSqlQueryFailureTest extends AbstractPostLoginApiTest {
    protected Datasource addedDatasource;
    protected SqlQueryDto dto;
    protected SqlQueryDao dao;

    @Before
    public void setUpAddQueryRunFailureTest() {
        addedDatasource = datasource();
        getInjector().getInstance(DatasourceDao.class).save(addedDatasource);

        dao = getInjector().getInstance(SqlQueryDao.class);
        dto = queryRunDto();
        dto.setDatasourceId(addedDatasource.getId());

        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setCronExpression(dto.getCronExpression());
        sqlQuery.setQuery(dto.getQuery());
        sqlQuery.setLabel(dto.getLabel());
        sqlQuery.setDatasource(addedDatasource);

        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);
    }

    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(Routes.ADD_SQL_QUERY_API), dto)),
                format(Messages.QUERY_RUN_ADDITION_FAILURE_M, dto.getLabel())
        );
    }
}
