package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerSaveJobWithDependentsTest extends AbstractPostLoginApiTest {
    protected JobModel jobModel;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected Datasource datasource;

    @Before
    public void setUp() {
        jobModel = TestUtil.jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobModel.setSqlQueries(new HashSet<>());

        datasource = new Datasource();
        datasource.setId(1);
        datasource.setLabel("mysql");
        datasource.setPassword("root");
        datasource.setUsername("root");
        datasource.setUrl("localhost");
        datasource.setPort(3306);
        datasource.setType(MYSQL);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);

        jobModel.getSqlQueries().add(sqlQueryModel);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        jobDbSetUp(jobModel);
        sqlQueryDbSetUp(sqlQueryModel);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(JOB_SQL_QUERY_TABLE)
                                .row()
                                .column(ID_COLUMN, sqlQueryModel.getId())
                                .column(JOB_ID_FK_COLUMN, jobModel.getId())
                                .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                .end()
                                .build()
                )
        ).launch();
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setParentJobId(jobModel.getId());
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setCronExpression(null);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select * from mysql.user");
        sqlQueryDto.setDatasourceId(datasource.getId());

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        JobModel savedJobModel = new JobModel();
        savedJobModel.setId(TestUtil.DB_START_ID);
        savedJobModel.setCronExpression("");
        savedJobModel.setLabel(jobDto.getLabel());
        savedJobModel.setTitle(jobDto.getTitle());

        assertDbState(JobModel.JOB_TABLE, jobTable(ImmutableList.of(savedJobModel, jobModel)));

        jobModel.setChildJobs(ImmutableSet.of(savedJobModel));

        assertDbState(JobModel.JOB_CHILDREN_TABLE, jobDependentTable(jobModel), "id");
    }
}
