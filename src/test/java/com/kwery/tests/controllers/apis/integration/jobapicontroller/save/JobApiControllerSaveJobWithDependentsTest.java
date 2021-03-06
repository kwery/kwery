package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerSaveJobWithDependentsTest extends AbstractPostLoginApiTest {
    protected JobModel jobModel;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected Datasource datasource;

    JobDao jobDao;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");

        datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);

        jobModel.getSqlQueries().add(sqlQueryModel);

        jobDbSetUp(jobModel);
        sqlQueryDbSetUp(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        Set<String> emails = ImmutableSet.of("foo@bar.com", "goo@boo.com");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setParentJobId(jobModel.getId());
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setCronExpression(null);
        jobDto.setEmails(emails);

        Set<String> alertEmails = ImmutableSet.of("foo@cho.com", "roo@goo.com");
        jobDto.setJobFailureAlertEmails(alertEmails);

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setName(jobDto.getName());
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setChildJobs(new HashSet<>());
        expectedJobModel.setCronExpression("");
        expectedJobModel.setFailureAlertEmails(alertEmails);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select * from mysql.user");
        sqlQueryDto.setDatasourceId(datasource.getId());

        SqlQueryModel expectedSqlQueryModel = new SqlQueryModel();
        expectedSqlQueryModel.setLabel(sqlQueryDto.getLabel());
        expectedSqlQueryModel.setTitle(sqlQueryDto.getTitle());
        expectedSqlQueryModel.setQuery(sqlQueryDto.getQuery());
        expectedSqlQueryModel.setDatasource(datasource);

        expectedJobModel.setSqlQueries(ImmutableList.of(expectedSqlQueryModel));

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());

        expectedJobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));
        expectedJobModel.setParentJob(jobDao.getJobById(jobModel.getId()));

        JobModel jobModelFromDb = jobDao.getJobByName(jobDto.getName());

        assertThat(response, hasJsonPath("$.reportId", is(jobModelFromDb.getId())));

        assertThat(jobModelFromDb, theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("sqlQueries.id")
                .excludeProperty("created").excludeProperty("updated"));
    }
}
