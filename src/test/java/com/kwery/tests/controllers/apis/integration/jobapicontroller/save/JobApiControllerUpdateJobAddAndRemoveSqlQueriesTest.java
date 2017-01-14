package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobAddAndRemoveSqlQueriesTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();
    private JobModel jobModel;
    private Datasource datasource;
    private JobDao jobDao;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.setSqlQueries(ImmutableSet.of(sqlQueryModel));
        jobSqlQueryDbSetUp(jobModel);

        getInjector().getInstance(JobService.class).schedule(jobModel.getId());
        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() {
        JobDto jobDto = new JobDto();
        jobDto.setTitle(jobModel.getTitle());
        jobDto.setName(jobModel.getName());
        jobDto.setId(jobModel.getId());
        jobDto.setCronExpression(jobModel.getCronExpression());

        SqlQueryDto sqlQueryDto0 = sqlQueryDtoWithoutId();
        sqlQueryDto0.setDatasourceId(datasource.getId());

        SqlQueryDto sqlQueryDto1 = sqlQueryDtoWithoutId();
        sqlQueryDto1.setDatasourceId(datasource.getId());

        jobDto.setSqlQueries(ImmutableList.of(sqlQueryDto0, sqlQueryDto1));

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");
        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        jobModel.setSqlQueries(null);

        List<SqlQueryModel> expectedSqlQueryModels = new ArrayList<>();
        for (SqlQueryDto sqlQueryDto : ImmutableList.of(sqlQueryDto0, sqlQueryDto1)) {
            SqlQueryModel sqlQueryModel = new SqlQueryModel();
            sqlQueryModel.setDatasource(datasource);
            sqlQueryModel.setLabel(sqlQueryDto.getLabel());
            sqlQueryModel.setTitle(sqlQueryDto.getTitle());
            sqlQueryModel.setQuery(sqlQueryDto.getQuery());
            expectedSqlQueryModels.add(sqlQueryModel);
        }

        jobDto.setSqlQueries(ImmutableList.of(sqlQueryDto0, sqlQueryDto1));

        assertThat(response, is(isJson()));

        assertThat(jobDao.getAllJobs(), hasSize(1));

        JobModel jobModelFromDb = jobDao.getJobById(jobDto.getId());

        assertThat(jobModelFromDb.getSqlQueries(), hasSize(2));

        List<SqlQueryModel> sqlQueryModelsFromDb = new ArrayList<>(jobModelFromDb.getSqlQueries());

        jobModelFromDb.setSqlQueries(null);

        sort(expectedSqlQueryModels, comparing(SqlQueryModel::getLabel));
        sort(sqlQueryModelsFromDb, comparing(SqlQueryModel::getLabel));

        assertThat(jobModel, theSameBeanAs(jobModelFromDb));

        assertThat(sqlQueryModelsFromDb.get(0), theSameBeanAs(expectedSqlQueryModels.get(0)).excludeProperty("id"));
        assertThat(sqlQueryModelsFromDb.get(1), theSameBeanAs(expectedSqlQueryModels.get(1)).excludeProperty("id"));
    }
}
