package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import java.util.HashSet;
import java.util.Set;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerUpdateJobWithLabelTest extends AbstractPostLoginApiTest {
    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    private JobModel jobModel;
    private Datasource datasource1;
    private SqlQueryModel sqlQueryModel;

    JobDao jobDao;
    private JobLabelModel jobLabelModel0;
    private JobLabelModel jobLabelModel1;
    private JobLabelModel jobLabelModel2;

    @Before
    public void setUpJobApiControllerUpdateJobTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobDbSetUp(jobModel);

        Datasource datasource0 = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
        datasource0.setId(dbId());
        datasourceDbSetup(datasource0);

        sqlQueryModel = sqlQueryModel(datasource0);
        sqlQueryModel.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        jobModel.getEmails().addAll(ImmutableSet.of("foo@bar.com", "goo@boo.com"));
        jobEmailDbSetUp(jobModel);

        datasource1 = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
        datasource1.setLabel("mysql0");
        datasource1.setId(dbId());
        datasourceDbSetup(datasource1);

        getInjector().getInstance(JobService.class).schedule(jobModel.getId());

        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        Set<JobLabelModel> labels = ImmutableSet.of(jobLabelModel0, jobLabelModel1);
        jobModel.setLabels(labels);

        jobJobLabelDbSetUp(jobModel);

        jobLabelModel2 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel2);

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
        jobDto.setLabelIds(ImmutableSet.of(jobLabelModel1.getId(), jobLabelModel2.getId()));
        Set<String> alertEmails = ImmutableSet.of("foo@goo.com", "roo@moo.com");
        jobDto.setJobFailureAlertEmails(alertEmails);

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setName(jobDto.getName());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setChildJobs(new HashSet<>());
        expectedJobModel.setCronExpression(jobDto.getCronExpression());
        expectedJobModel.setLabels(ImmutableSet.of(jobLabelModel1, jobLabelModel2));
        expectedJobModel.setFailureAlertEmails(alertEmails);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select User from mysql.user where User = 'root'");
        sqlQueryDto.setDatasourceId(datasource1.getId());
        sqlQueryDto.setId(sqlQueryModel.getId());

        SqlQueryModel expectedSqlQueryModel = new SqlQueryModel();
        expectedSqlQueryModel.setTitle(sqlQueryDto.getTitle());
        expectedSqlQueryModel.setLabel(sqlQueryDto.getLabel());
        expectedSqlQueryModel.setDatasource(datasource1);
        expectedSqlQueryModel.setQuery(sqlQueryDto.getQuery());
        expectedJobModel.setSqlQueries(ImmutableList.of(expectedSqlQueryModel));

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.reportId", is(jobModel.getId())));

        expectedJobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));
        assertThat(jobDao.getJobByName(expectedJobModel.getName()), theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("queries.id")
                .excludeProperty("created").excludeProperty("updated"));
        assertThat(jobDao.getAllJobs(), hasSize(1));
    }
}
