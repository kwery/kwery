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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobAddAndRemoveEmailsTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();
    private JobModel jobModel;
    private Datasource datasource;

    String email0 = "foo@bar.com";
    String email1 = "bar@moo.com";
    String email2 = "cho@goo.com";
    private JobDao jobDao;
    private SqlQueryModel sqlQueryModel;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobModel.setEmails(ImmutableSet.of(email0));

        jobDbSetUp(jobModel);

        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        sqlQueryModel = sqlQueryModel(datasource);
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
        jobDto.setEmails(ImmutableSet.of(email1, email2));

        SqlQueryDto sqlQueryDto = new SqlQueryDto();
        sqlQueryDto.setTitle(sqlQueryModel.getTitle());
        sqlQueryDto.setLabel(sqlQueryModel.getLabel());
        sqlQueryDto.setDatasourceId(datasource.getId());
        sqlQueryDto.setQuery(sqlQueryModel.getQuery());
        sqlQueryDto.setId(sqlQueryModel.getId());

        jobDto.setSqlQueries(ImmutableList.of(sqlQueryDto));

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");
        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, is(isJson()));

        jobModel.setEmails(ImmutableSet.of(email1, email2));

        assertThat(jobDao.getAllJobs(), hasSize(1));
        assertThat(jobDao.getJobById(jobDto.getId()), theSameBeanAs(jobModel));
    }
}
