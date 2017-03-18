package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.*;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.conf.KweryDirectory;
import ninja.Router;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.HashMap;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerReportAsCsvTest extends AbstractPostLoginApiTest {

    private SqlQueryExecutionModel sqlQueryExecutionModel;
    private String csv;

    @Before
    public void setUp() throws IOException {
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

        KweryDirectory kweryDirectory = getInjector().getInstance(KweryDirectory.class);

        File file = kweryDirectory.createFile();

        csv = String.join(System.lineSeparator(), "c", "v") + System.lineSeparator();

        Files.write(Paths.get(file.getPath()), csv.getBytes(), StandardOpenOption.APPEND);

        sqlQueryExecutionModel = sqlQueryExecutionModel();

        sqlQueryExecutionModel.setResultFileName(file.getName());

        sqlQueryExecutionModel.setExecutionStart(calendar.getTimeInMillis());

        sqlQueryExecutionModel.setSqlQuery(sqlQueryModel);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);
    }

    @Test
    public void test() throws IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "reportAsCsv", ImmutableMap.of("sqlQueryExecutionId", sqlQueryExecutionModel.getExecutionId()));
        HttpResponse response = ninjaTestBrowser.makeRequestAndGetResponse(getUrl(url), new HashMap<>());
        assertThat(fileContent(response), is(csv));

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
