package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobexecutions;

import com.google.common.collect.ImmutableList;
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
import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.controllers.apis.JobApiController.DISPLAY_DATE_FORMAT;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobExecutionsFilterByDateTest extends AbstractPostLoginApiTest {
    private JobModel jobModel;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        //Thu Jan 05 01:00:00 IST 2017 - 1483558200000
        //Thu Jan 05 02:00:00 IST 2017 - 1483561800000
        //Thu Jan 05 03:00:00 IST 2017 - 1483565400000
        //Thu Jan 05 04:00:00 IST 2017 - 1483569000000

        List<Long> epochs = ImmutableList.of(1483558200000L, 1483561800000L, 1483565400000L, 1483569000000L);

        for (Long epoch : epochs) {
            JobExecutionModel jobExecutionModel = jobExecutionModel();
            jobExecutionModel.setExecutionStart(epoch);
            jobExecutionModel.setJobModel(jobModel);
            jobExecutionDbSetUp(jobExecutionModel);
        }
    }

    @Test
    public void testGetAll() throws ParseException {
        JobExecutionListFilterDto filter = new JobExecutionListFilterDto();

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobExecutions",
                ImmutableMap.of("jobId", jobModel.getId()));

        filter.setResultCount(2);
        filter.setPageNumber(0);
        filter.setExecutionStartStart("Thu Jan 04 2017 23:00");
        filter.setExecutionStartEnd("Thu Jan 05 2017 05:00");

        String response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertOrdering(response);
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(2)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));

        filter.setResultCount(2);
        filter.setPageNumber(1);
        filter.setExecutionStartStart("Thu Jan 04 2017 23:00");
        filter.setExecutionStartEnd("Thu Jan 05 2017 05:00");

        response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertOrdering(response);
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(2)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));

        filter.setResultCount(2);
        filter.setPageNumber(2);
        filter.setExecutionStartStart("Thu Jan 04 2017 23:00");
        filter.setExecutionStartEnd("Thu Jan 05 2017 05:00");

        response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertOrdering(response);
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(0)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));
    }

    @Test
    public void testRange() throws ParseException {
        JobExecutionListFilterDto filter = new JobExecutionListFilterDto();

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobExecutions",
                ImmutableMap.of("jobId", jobModel.getId()));

        filter.setResultCount(1);
        filter.setPageNumber(0);
        filter.setExecutionStartStart("Thu Jan 05 2017 00:00");
        filter.setExecutionStartEnd("Thu Jan 05 2017 02:30");

        String response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertOrdering(response);
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));

        filter.setResultCount(1);
        filter.setPageNumber(1);

        response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertOrdering(response);
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));

        filter.setResultCount(2);
        filter.setPageNumber(1);

        response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertOrdering(response);
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(0)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));
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
