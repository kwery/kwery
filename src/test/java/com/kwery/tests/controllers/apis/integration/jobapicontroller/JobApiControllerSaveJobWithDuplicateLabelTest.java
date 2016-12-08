package com.kwery.tests.controllers.apis.integration.jobapicontroller;

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
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.ID_COLUMN;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_REPORT_LABEL_EXISTS_M;
import static com.kwery.tests.util.Messages.JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.failure;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JobApiControllerSaveJobWithDuplicateLabelTest extends AbstractPostLoginApiTest {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;

    protected String jobLabel = "label";
    protected String queryLabel = "label";

    @Before
    public void setUpJobApiControllerSaveJobWithDuplicateLabelTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(new HashSet<>());
        jobModel.setLabel(jobLabel);

        datasource = datasource();

        SqlQueryModel sqlQueryModel = sqlQueryModel();
        sqlQueryModel.setDependentQueries(null);
        sqlQueryModel.setRecipientEmails(null);
        sqlQueryModel.setCronExpression(null);
        sqlQueryModel.setDatasource(datasource);
        sqlQueryModel.setLabel(queryLabel);
        jobModel.getSqlQueries().add(sqlQueryModel);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.insertInto(JOB_TABLE)
                        .row()
                        .column(JobModel.ID_COLUMN, jobModel.getId())
                        .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel.getCronExpression())
                        .column(JobModel.LABEL_COLUMN, jobModel.getLabel())
                        .end()
                        .build()
        ).launch();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        Operations.insertInto(SQL_QUERY_TABLE)
                                .row()
                                .column(ID_COLUMN, sqlQueryModel.getId())
                                .column(SqlQueryModel.LABEL_COLUMN, sqlQueryModel.getLabel())
                                .column(SqlQueryModel.QUERY_COLUMN, sqlQueryModel.getQuery())
                                .column(SqlQueryModel.DATASOURCE_ID_FK_COLUMN, sqlQueryModel.getDatasource().getId())
                                .end()
                                .build(),
                        Operations.insertInto(JOB_SQL_QUERY_TABLE)
                                .row()
                                .column(JobModel.ID_COLUMN, 1)
                                .column(JobModel.JOB_ID_FK_COLUMN, jobModel.getId())
                                .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                .end()
                                .build()
                )
        ).launch();

        jobDao = getInjector().getInstance(JobDao.class);
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "saveJob");

        JobDto jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setLabel(jobLabel);

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
        actionResult.setMessages(ImmutableList.of(format(JOBAPICONTROLLER_REPORT_LABEL_EXISTS_M, jobLabel), format(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M, queryLabel)));

        assertEquals(toJson(actionResult), response, false);
    }
}
