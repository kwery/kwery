package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableSet;
import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize;
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

import java.util.HashSet;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    private JobModel jobModel;
    private Datasource datasource1;
    private SqlQueryModel sqlQueryModel;

    JobDao jobDao;

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

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        ImmutableSet<String> emails = ImmutableSet.of("foo@bar.com", "goo@moo.com");
        jobDto.setEmails(emails);
        jobDto.setId(jobModel.getId());
        jobDto.setParentJobId(0);

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setLabel(jobDto.getLabel());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setChildJobs(new HashSet<>());
        expectedJobModel.setCronExpression(jobDto.getCronExpression());

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDto.setDatasourceId(datasource1.getId());
        sqlQueryDto.setId(sqlQueryModel.getId());

        SqlQueryModel expectedSqlQueryModel = new SqlQueryModel();
        expectedSqlQueryModel.setTitle(sqlQueryDto.getTitle());
        expectedSqlQueryModel.setLabel(sqlQueryDto.getLabel());
        expectedSqlQueryModel.setDatasource(datasource1);
        expectedSqlQueryModel.setQuery(sqlQueryDto.getQuery());
        expectedJobModel.setSqlQueries(ImmutableSet.of(expectedSqlQueryModel));

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        assertThat(jobDao.getJobByLabel(expectedJobModel.getLabel()), theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("queries.id"));
        assertThat(jobDao.getAllJobs(), hasSize(1));
    }
}
