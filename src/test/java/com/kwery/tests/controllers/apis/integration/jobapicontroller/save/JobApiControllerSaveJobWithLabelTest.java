package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

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
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.MysqlDockerRule;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerSaveJobWithLabelTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected Datasource datasource;

    protected JobDao jobDao;
    private Set<Integer> labelIds;
    private Set<JobLabelModel> labels;

    @Before
    public void setUpJobApiControllerSaveJobTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        jobDao = getInjector().getInstance(JobDao.class);

        JobLabelModel jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        JobLabelModel jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        labels = ImmutableSet.of(jobLabelModel0, jobLabelModel1);
        labelIds = ImmutableSet.of(jobLabelModel0.getId(), jobLabelModel1.getId());
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
        jobDto.setLabelIds(labelIds);

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setCronExpression(jobDto.getCronExpression());
        expectedJobModel.setName(jobDto.getName());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setSqlQueries(new LinkedList<>());
        expectedJobModel.setChildJobs(new HashSet<>());
        expectedJobModel.setLabels(labels);

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

        JobModel jobModel = jobDao.getJobByName(jobDto.getName());

        expectedJobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));
        assertThat(jobModel, theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("sqlQueries.id"));
    }
}
