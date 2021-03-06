package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryEmailSettingModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.util.TestUtil.jobDtoWithoutId;
import static com.kwery.tests.util.TestUtil.sqlQueryDtoWithoutId;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerSaveJobTest extends AbstractPostLoginApiTest {
    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    protected Datasource datasource;

    protected JobDao jobDao;

    @Before
    public void setUpJobApiControllerSaveJobTest() {
        datasource = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
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
        ImmutableSet<String> alertEmails = ImmutableSet.of("goo@loo.com", "roo@gro.com");
        jobDto.setJobFailureAlertEmails(alertEmails);

        JobModel expectedJobModel = new JobModel();
        expectedJobModel.setTitle(jobDto.getTitle());
        expectedJobModel.setCronExpression(jobDto.getCronExpression());
        expectedJobModel.setName(jobDto.getName());
        expectedJobModel.setEmails(emails);
        expectedJobModel.setSqlQueries(new LinkedList<>());
        expectedJobModel.setChildJobs(new HashSet<>());
        expectedJobModel.setFailureAlertEmails(alertEmails);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select * from mysql.user");
        sqlQueryDto.setDatasourceId(datasource.getId());

        SqlQueryEmailSettingModel emailSettingModel = TestUtil.sqlQueryEmailSettingModelWithoutId();
        sqlQueryDto.setSqlQueryEmailSetting(emailSettingModel);

        SqlQueryModel expectedSqlQueryModel = new SqlQueryModel();
        expectedSqlQueryModel.setQuery(sqlQueryDto.getQuery());
        expectedSqlQueryModel.setLabel(sqlQueryDto.getLabel());
        expectedSqlQueryModel.setDatasource(datasource);
        expectedSqlQueryModel.setTitle(sqlQueryDto.getTitle());
        expectedSqlQueryModel.setSqlQueryEmailSettingModel(emailSettingModel);

        expectedJobModel.getSqlQueries().add(expectedSqlQueryModel);

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.reportId", is(jobDao.getAllJobs().get(0).getId())));

        JobModel jobModel = jobDao.getJobByName(jobDto.getName());

        expectedJobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));
        assertThat(jobModel, theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("sqlQueries.id")
                .excludeProperty("sqlQueries.sqlQueryEmailSetting.id").excludeProperty("created").excludeProperty("updated"));
    }
}
