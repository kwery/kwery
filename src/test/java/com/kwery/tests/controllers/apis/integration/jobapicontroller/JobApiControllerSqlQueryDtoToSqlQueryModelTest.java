package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryEmailSettingModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.*;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobApiControllerSqlQueryDtoToSqlQueryModelTest extends RepoDashDaoTestBase {
    protected JobApiController jobApiController;
    protected DatasourceDao datasourceDao;
    private Datasource datasource = null;

    @Before
    public void setUpJobApiControllerJobDtoToJobModelTest() {
        datasource = datasource();
        DbUtil.datasourceDbSetup(datasource);

        datasourceDao = getInstance(DatasourceDao.class);

        jobApiController = new JobApiController(datasourceDao, getInstance(JobDao.class), null, null, null, null, null, null, null);
    }

    @Test
    public void testNullEmailSetting() {
        SqlQueryDto dto = sqlQueryDto();
        dto.setDatasourceId(datasource.getId());

        SqlQueryModel model = new SqlQueryModel();
        model.setLabel(dto.getLabel());
        model.setQuery(dto.getQuery());
        model.setId(dto.getId());
        Datasource datasourceFromDb = datasourceDao.getById(datasource.getId());
        model.setDatasource(datasourceFromDb);
        model.setTitle(dto.getTitle());

        assertThat(jobApiController.sqlQueryDtoToSqlQueryModel(dto), theSameBeanAs(model));
    }

    @Test
    public void test() {
        SqlQueryDto dto = sqlQueryDto();
        dto.setDatasourceId(datasource.getId());

        SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModel();
        dto.setSqlQueryEmailSetting(sqlQueryEmailSettingModel);

        SqlQueryModel model = new SqlQueryModel();
        model.setLabel(dto.getLabel());
        model.setQuery(dto.getQuery());
        model.setId(dto.getId());
        Datasource datasourceFromDb = datasourceDao.getById(datasource.getId());
        model.setDatasource(datasourceFromDb);
        model.setTitle(dto.getTitle());
        model.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel);

        assertThat(jobApiController.sqlQueryDtoToSqlQueryModel(dto), theSameBeanAs(model));
    }

    @Test
    public void testNullId() {
        SqlQueryDto dto = sqlQueryDtoWithoutId();

        String label = "foo";
        dto.setLabel(label);

        String query = "bar";
        dto.setQuery(query);

        dto.setDatasourceId(datasource.getId());

        SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModelWithoutId();
        dto.setSqlQueryEmailSetting(sqlQueryEmailSettingModel);

        SqlQueryModel model = new SqlQueryModel();
        model.setLabel(label);
        model.setQuery(query);
        Datasource datasourceFromDb = datasourceDao.getById(datasource.getId());
        model.setDatasource(datasourceFromDb);
        model.setTitle(dto.getTitle());
        model.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel);

        assertThat(jobApiController.sqlQueryDtoToSqlQueryModel(dto), theSameBeanAs(model));
    }
}
