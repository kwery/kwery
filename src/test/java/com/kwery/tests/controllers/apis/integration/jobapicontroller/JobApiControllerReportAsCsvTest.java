package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.*;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerReportAsCsvTest extends AbstractPostLoginApiTest {

    private SqlQueryExecutionModel sqlQueryExecutionModel;

    @Before
    public void setUp() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setTitle("  csv   test file  ");
        sqlQueryDbSetUp(sqlQueryModel);

        jobModel.getSqlQueries().add(sqlQueryModel);

        jobSqlQueryDbSetUp(jobModel);

        JobExecutionModel jobExecutionModel = jobExecutionModel();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.JANUARY, 4);
        jobExecutionModel.setExecutionStart(calendar.getTimeInMillis());
        jobExecutionModel.setJobModel(jobModel);

        jobExecutionDbSetUp(jobExecutionModel);

        sqlQueryExecutionModel = sqlQueryExecutionModel();

        sqlQueryExecutionModel.setExecutionStart(calendar.getTimeInMillis());

        String json = toJson(
                ImmutableList.of(
                        ImmutableList.of("c"),
                        ImmutableList.of("v")
                )
        );
        sqlQueryExecutionModel.setResult(json);
        sqlQueryExecutionModel.setSqlQuery(sqlQueryModel);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);
    }

    @Test
    public void test() throws IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "reportAsCsv", ImmutableMap.of("sqlQueryExecutionId", sqlQueryExecutionModel.getExecutionId()));
        HttpResponse response = ninjaTestBrowser.makeRequestAndGetResponse(getUrl(url), new HashMap<>());
        String expected = "\"c\"\n\"v\"\n";
        assertThat(fileContent(response), is( expected));

        String expectedFileName = "csv-test-file-wed-jan-04.csv";

        assertThat(response.getFirstHeader("Content-Disposition").getValue(), is("attachment; filename=" + expectedFileName));
        assertThat(response.getFirstHeader("Content-Type").getValue(), is("text/csv; charset=UTF-8"));

    }

    private String fileContent(HttpResponse httpResponse) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(httpResponse.getEntity().getContent())) {
            return CharStreams.toString(isr);
        }
    }
}
