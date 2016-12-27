package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobWithDependentsTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    private Datasource datasource1;

    private DatasourceDao datasourceDao;
    private JobModel childJob;
    private SqlQueryModel childSqlQueryModel;
    private JobModel parentJobModel0;
    private JobModel parentJobModel1;

    @Before
    public void setUpJobApiControllerUpdateJobWithDependentsTest() {
        parentJobModel0 = jobModelWithoutDependents();
        parentJobModel0.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel0);

        Datasource datasource0 = mysqlDockerRule.getMySqlDocker().datasource();
        datasource0.setId(dbId());
        datasourceDbSetup(datasource0);

        SqlQueryModel sqlQueryModel0 = sqlQueryModel(datasource0);
        sqlQueryModel0.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(sqlQueryModel0);

        parentJobModel0.getSqlQueries().add(sqlQueryModel0);
        jobSqlQueryDbSetUp(parentJobModel0);

        parentJobModel0.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel0);

        datasource1 = mysqlDockerRule.getMySqlDocker().datasource();
        datasource1.setLabel("mysql0");
        datasource1.setId(dbId());
        datasourceDbSetup(datasource1);

        childJob = jobModelWithoutDependents();
        jobDbSetUp(childJob);

        parentJobModel0.getDependentJobs().add(childJob);
        jobDependentDbSetUp(parentJobModel0);

        childSqlQueryModel = sqlQueryModel(datasource0);
        childSqlQueryModel.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(childSqlQueryModel);

        childJob.getSqlQueries().add(childSqlQueryModel);
        jobSqlQueryDbSetUp(childJob);

        childJob.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(childJob);


        parentJobModel1 = jobModelWithoutDependents();
        parentJobModel1.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel1);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel(datasource0);
        sqlQueryModel1.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(sqlQueryModel1);

        parentJobModel1.getSqlQueries().add(sqlQueryModel1);
        jobSqlQueryDbSetUp(parentJobModel1);

        parentJobModel1.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(parentJobModel1);

        JobService jobService = getInjector().getInstance(JobService.class);
        jobService.schedule(parentJobModel0.getId());
        jobService.schedule(parentJobModel1.getId());

        datasourceDao = getInjector().getInstance(DatasourceDao.class);
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setEmails(ImmutableSet.of("foo@bar.com", "goo@moo.com"));
        jobDto.setId(childJob.getId());
        jobDto.setParentJobId(parentJobModel1.getId());

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDto.setDatasourceId(datasource1.getId());
        sqlQueryDto.setId(childSqlQueryModel.getId());

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        JobModel expectedJobModel = new JobApiController(datasourceDao, null, null, null, null, null)
                .jobDtoToJobModel(jobDto);
        new DbTableAsserterBuilder(JOB_TABLE, jobTable(ImmutableList.of(expectedJobModel, parentJobModel0, parentJobModel1))).build().assertTable();

        List<SqlQueryModel> sqlQueries = new LinkedList<>();
        Stream.of(expectedJobModel.getSqlQueries(), parentJobModel0.getSqlQueries(), parentJobModel1.getSqlQueries()).forEach(sqlQueries::addAll);

        new DbTableAsserterBuilder(SQL_QUERY_TABLE, sqlQueryTable(sqlQueries)).build().assertTable();

        parentJobModel0.getDependentJobs().clear();

        new DbTableAsserterBuilder(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(expectedJobModel, parentJobModel0, parentJobModel1)).columnToIgnore(JOB_SQL_QUERY_TABLE_ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_EMAIL_TABLE, jobEmailTable(expectedJobModel, parentJobModel0, parentJobModel1)).columnToIgnore(JOB_EMAIL_ID_COLUMN).build().assertTable();
    }
}
