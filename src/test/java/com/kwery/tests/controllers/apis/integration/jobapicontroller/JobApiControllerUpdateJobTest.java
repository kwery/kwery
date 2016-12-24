package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    private JobModel jobModel;
    private Datasource datasource1;
    private SqlQueryModel sqlQueryModel;

    private DatasourceDao datasourceDao;

    @Before
    public void setUpJobApiControllerUpdateJobTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobDbSetUp(jobModel);

        Datasource datasource0 = mysqlDockerRule.getMySqlDocker().datasource();
        datasource0.setId(dbId());
        datasourceDbSetup(datasource0);

        sqlQueryModel = sqlQueryModel(datasource0);
        sqlQueryModel.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        jobModel.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(jobModel);

        datasource1 = mysqlDockerRule.getMySqlDocker().datasource();
        datasource1.setLabel("mysql0");
        datasource1.setId(dbId());
        datasourceDbSetup(datasource1);

        getInjector().getInstance(JobService.class).schedule(jobModel.getId());

        datasourceDao = getInjector().getInstance(DatasourceDao.class);
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setEmails(ImmutableSet.of("foo@bar.com", "goo@moo.com"));
        jobDto.setId(jobModel.getId());
        jobDto.setParentJobId(0);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDto.setDatasourceId(datasource1.getId());
        sqlQueryDto.setId(sqlQueryModel.getId());

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        JobModel expectedJobModel = new JobApiController(datasourceDao, null, null, null, null, null).jobDtoToJobModel(jobDto);
        new DbTableAsserterBuilder(JOB_TABLE, jobTable(expectedJobModel)).build().assertTable();

        new DbTableAsserterBuilder(SQL_QUERY_TABLE, sqlQueryTable(expectedJobModel.getSqlQueries())).build().assertTable();
        new DbTableAsserterBuilder(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(expectedJobModel)).columnToIgnore(JOB_SQL_QUERY_TABLE_ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_EMAIL_TABLE, jobEmailTable(expectedJobModel)).columnToIgnore(JOB_EMAIL_ID_COLUMN).build().assertTable();
    }
}
