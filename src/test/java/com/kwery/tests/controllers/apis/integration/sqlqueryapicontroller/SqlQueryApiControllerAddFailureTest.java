package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.kwery.conf.Routes;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.Messages;

import java.io.IOException;

import static java.text.MessageFormat.format;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.queryRunDto;

public class SqlQueryApiControllerAddFailureTest extends AbstractPostLoginApiTest {
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
