package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobexecutions;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionListFilterDto;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.controllers.apis.JobApiController.DISPLAY_DATE_FORMAT;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobExecutionsTest extends AbstractPostLoginApiTest {
    private JobModel jobModel;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        for (int i = 0; i < 4; ++i) {
            JobExecutionModel jobExecutionModel = jobExecutionModel();
            jobExecutionModel.setJobModel(jobModel);
            jobExecutionDbSetUp(jobExecutionModel);
        }
    }

    @Test
    public void test() throws Exception {
        JobExecutionListFilterDto filter = new JobExecutionListFilterDto();

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobExecutions",
                ImmutableMap.of("jobId", jobModel.getId()));

        //First page
        filter.setResultCount(2);
        filter.setPageNumber(0);
        String response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(2)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));
        assertOrdering(response);

        //Second page
        filter.setResultCount(2);
        filter.setPageNumber(1);
        response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(2)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));
        assertOrdering(response);

        //Third page
        filter.setResultCount(2);
        filter.setPageNumber(2);
        response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(0)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));
        assertOrdering(response);

        //All in one page
        filter.setResultCount(100);
        filter.setPageNumber(0);
        response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(4)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));
        assertOrdering(response);
    }

    protected void assertOrdering(String response) throws ParseException {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(response);
        int length = JsonPath.read(document, "$.jobExecutionDtos.length()");
        if (length > 1) {
            for (int i = 1; i < length; ++i) {
                long first = new SimpleDateFormat(DISPLAY_DATE_FORMAT).parse(JsonPath.read(document, String.format("$.jobExecutionDtos[%d].start", i - 1))).getTime();
                long second = new SimpleDateFormat(DISPLAY_DATE_FORMAT).parse(JsonPath.read(document, String.format("$.jobExecutionDtos[%d].start", i))).getTime();
                assertThat(first, is(greaterThanOrEqualTo(second)));
            }
        }
    }
}
