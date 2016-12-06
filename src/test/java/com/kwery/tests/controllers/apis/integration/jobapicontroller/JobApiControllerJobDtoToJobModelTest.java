package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobApiControllerJobDtoToJobModelTest extends RepoDashDaoTestBase {
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

        jobApiController = new JobApiController(datasourceDao, getInstance(JobDao.class), null, null);
    }

    @Test
    public void testWithoutId() {
        JobDto jobDto = jobDtoWithoutId();
        jobDto.setSqlQueries(new ArrayList<>(2));

        JobModel jobModel = new JobModel();
        jobModel.setLabel(jobDto.getLabel());
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setId(null);
        jobModel.setSqlQueries(new HashSet<>(2));

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
            sqlQueryDto.setDatasourceId(datasourceId);
            jobDto.getSqlQueries().add(sqlQueryDto);

            SqlQueryModel model = new SqlQueryModel();
            model.setQuery(sqlQueryDto.getQuery());
            model.setLabel(sqlQueryDto.getLabel());
            model.setDatasource(datasourceDao.getById(datasourceId));

            jobModel.getSqlQueries().add(model);
        }

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }

    @Test
    public void testWithId() {
        JobDto jobDto = TestUtil.jobDto();
        jobDto.setSqlQueries(new ArrayList<>(2));

        JobModel jobModel = new JobModel();
        jobModel.setLabel(jobDto.getLabel());
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setId(null);
        jobModel.setSqlQueries(new HashSet<>(2));
        jobModel.setId(jobDto.getId());

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDto();
            sqlQueryDto.setDatasourceId(datasourceId);
            jobDto.getSqlQueries().add(sqlQueryDto);

            SqlQueryModel model = new SqlQueryModel();
            model.setQuery(sqlQueryDto.getQuery());
            model.setLabel(sqlQueryDto.getLabel());
            model.setDatasource(datasourceDao.getById(datasourceId));
            model.setId(sqlQueryDto.getId());

            jobModel.getSqlQueries().add(model);
        }

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }
}
