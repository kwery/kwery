package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.util.TestUtil.jobDtoWithoutId;
import static com.kwery.tests.util.TestUtil.sqlQueryDtoWithoutId;
import static com.kwery.views.ActionResult.Status.success;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerSaveJobTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;

    protected JobDao jobDao;

    @Before
    public void setUpJobApiControllerSaveJobTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setParentJobId(0);
        ImmutableSet<String> emails = ImmutableSet.of("foo@bar.com", "goo@moo.com");
        jobDto.setEmails(emails);

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setCronExpression(jobDto.getCronExpression());
        expectedJobModel.setLabel(jobDto.getLabel());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setSqlQueries(new HashSet<>());
        expectedJobModel.setChildJobs(new HashSet<>());

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select * from mysql.user");
        sqlQueryDto.setDatasourceId(datasource.getId());

        SqlQueryModel expectedSqlQueryModel = new SqlQueryModel();
        expectedSqlQueryModel.setQuery(sqlQueryDto.getQuery());
        expectedSqlQueryModel.setLabel(sqlQueryDto.getLabel());
        expectedSqlQueryModel.setDatasource(datasource);
        expectedSqlQueryModel.setTitle(sqlQueryDto.getTitle());

        expectedJobModel.getSqlQueries().add(expectedSqlQueryModel);

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        JobModel jobModel = jobDao.getJobByLabel(jobDto.getLabel());

        assertThat(jobModel, theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("sqlQueries.id"));
    }
}
