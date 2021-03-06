package com.kwery.tests.controllers.apis.integration.jobapicontroller.save;

import com.google.common.collect.ImmutableList;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.failure;
import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JobApiControllerSaveJobServerValidationTest extends AbstractPostLoginApiTest {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;

    protected String jobLabel = "label";
    protected String queryLabel = "label";

    @Before
    public void setUpJobApiControllerSaveJobWithDuplicateLabelTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setName(jobLabel);

        datasource = datasource();

        SqlQueryModel sqlQueryModel = sqlQueryModel();
        sqlQueryModel.setDatasource(datasource);
        sqlQueryModel.setLabel(queryLabel);
        jobModel.getSqlQueries().add(sqlQueryModel);

        datasourceDbSetup(datasource);
        jobDbSetUp(jobModel);
        sqlQueryDbSetUp(sqlQueryModel);
        jobSqlQueryDbSetUp(jobModel);

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        String invalidCron = "foo bar moo";

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setParentJobId(0);
        jobDto.setCronExpression(invalidCron);
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setName(jobLabel);

        SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
        sqlQueryDto.setQuery("select * from mysql.user");
        sqlQueryDto.setDatasourceId(datasource.getId());
        sqlQueryDto.setLabel(queryLabel);

        jobDto.getSqlQueries().add(sqlQueryDto);

        String response = ninjaTestBrowser.postJson(getUrl(url), jobDto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));

        ActionResult actionResult = new ActionResult();
        actionResult.setStatus(ActionResult.Status.failure);
        actionResult.setMessages(ImmutableList.of(
                format(JOBAPICONTROLLER_REPORT_NAME_EXISTS_M, jobLabel),
                format(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M, queryLabel),
                format(JOBLABELAPICONTROLLER_INVALID_CRON_EXPRESSION_M, invalidCron)
        ));

        assertEquals(toJson(actionResult), response, false);
    }
}
