package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobexecutions;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionListFilterDto;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
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
    public void test() {
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

        //Second page
        filter.setResultCount(2);
        filter.setPageNumber(1);
        response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(2)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));

        //Third page
        filter.setResultCount(2);
        filter.setPageNumber(2);
        response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(0)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));

        //All in one page
        filter.setResultCount(100);
        filter.setPageNumber(0);
        response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(4)));
        assertThat(response, hasJsonPath("$.totalCount", is(4)));
    }
}
