package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.util.TestUtil.sqlQueryDto;
import static com.kwery.tests.util.TestUtil.sqlQueryDtoWithoutId;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobApiControllerSqlQueryDtoToSqlQueryModelTest extends RepoDashDaoTestBase {
    protected JobApiController jobApiController;
    protected DatasourceDao datasourceDao;

    protected int datasourceId = 1;

    @Before
    public void setUpJobApiControllerJobDtoToJobModelTest() {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasourceId, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "username")
                                .build()
                )
        ).launch();

        datasourceDao = getInstance(DatasourceDao.class);

        jobApiController = new JobApiController(datasourceDao, getInstance(JobDao.class), null, null, null, null);
    }

    @Test
    public void test() {
        SqlQueryDto dto = sqlQueryDto();
        dto.setDatasourceId(datasourceId);

        SqlQueryModel model = new SqlQueryModel();
        model.setLabel(dto.getLabel());
        model.setQuery(dto.getQuery());
        model.setId(dto.getId());
        Datasource datasource = datasourceDao.getById(datasourceId);
        model.setDatasource(datasource);

        assertThat(jobApiController.sqlQueryDtoToSqlQueryModel(dto), theSameBeanAs(model));
    }

    @Test
    public void testNullId() {
        SqlQueryDto dto = sqlQueryDtoWithoutId();

        String label = "foo";
        dto.setLabel(label);

        String query = "bar";
        dto.setQuery(query);

        dto.setDatasourceId(datasourceId);

        SqlQueryModel model = new SqlQueryModel();
        model.setLabel(label);
        model.setQuery(query);
        Datasource datasource = datasourceDao.getById(datasourceId);
        model.setDatasource(datasource);

        assertThat(jobApiController.sqlQueryDtoToSqlQueryModel(dto), theSameBeanAs(model));
    }
}
