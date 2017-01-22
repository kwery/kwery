package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerSaveJobCronExpressionToChildJobTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();
    private JobModel parentJobModel;
    private SqlQueryModel sqlQueryModel;
    private SqlQueryModel parentSqlQueryModel;
    private JobModel jobModel;
    private Datasource datasource;
    private JobDao jobDao;

    @Before
    public void setUpJobApiControllerSaveJobCronExpressionToChildJobTest() {
        parentJobModel = jobModelWithoutDependents();
        parentJobModel.setCronExpression("* * * * *");
        jobDbSetUp(parentJobModel);

        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(DbUtil.dbId());
        datasourceDbSetup(datasource);

        parentSqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(parentSqlQueryModel);

        parentJobModel.getSqlQueries().add(parentSqlQueryModel);

        jobSqlQueryDbSetUp(parentJobModel);

        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobDbSetUp(jobModel);

        sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);

        jobSqlQueryDbSetUp(jobModel);

        JobService jobService = getInjector().getInstance(JobService.class);
        jobService.schedule(parentJobModel.getId());
        jobService.schedule(jobModel.getId());

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() {
        JobDto jobDto = new JobDto();
        jobDto.setTitle(jobModel.getTitle());
        jobDto.setName(jobModel.getName());
        jobDto.setId(jobModel.getId());
        jobDto.setParentJobId(parentJobModel.getId());

        SqlQueryDto sqlQueryDto = new SqlQueryDto();
        sqlQueryDto.setTitle(sqlQueryModel.getTitle());
        sqlQueryDto.setLabel(sqlQueryModel.getLabel());
        sqlQueryDto.setQuery(sqlQueryModel.getQuery());
        sqlQueryDto.setId(sqlQueryModel.getId());
        sqlQueryDto.setDatasourceId(datasource.getId());

        jobDto.setSqlQueries(ImmutableList.of(sqlQueryDto));

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");
        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, is(JsonPathMatchers.isJson()));

        parentJobModel.getChildJobs().add(jobModel);
        jobModel.setParentJob(parentJobModel);
        jobModel.setCronExpression(null);

        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        assertThat(jobModel, theSameBeanAs(jobDao.getJobById(jobDto.getId())));
        assertThat(parentJobModel, theSameBeanAs(jobDao.getJobById(jobDto.getParentJobId())));

        assertThat(jobDao.getAllJobs(), hasSize(2));
    }
}
