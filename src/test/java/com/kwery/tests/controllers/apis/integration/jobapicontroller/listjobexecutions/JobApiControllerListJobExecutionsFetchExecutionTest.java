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

import java.util.UUID;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobExecutionsFetchExecutionTest extends AbstractPostLoginApiTest {
    private JobModel jobModel;
    private String executionId;
    private JobExecutionModel jobExecutionModel0;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobExecutionModel0 = jobExecutionModel();
        jobExecutionModel0.setJobModel(jobModel);
        executionId = UUID.randomUUID().toString();
        jobExecutionModel0.setExecutionId(executionId);
        jobExecutionDbSetUp(jobExecutionModel0);


        JobExecutionModel jobExecutionModel1 = jobExecutionModel();
        jobExecutionModel1.setJobModel(jobModel);
        jobExecutionDbSetUp(jobExecutionModel1);
    }

    @Test
    public void test() {
        JobExecutionListFilterDto filter = new JobExecutionListFilterDto();

        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobExecutions",
                ImmutableMap.of("jobId", jobModel.getId()));

        //First page
        filter.setResultCount(1);
        filter.setPageNumber(0);
        filter.setExecutionId(executionId);
        String response = ninjaTestBrowser.postJson(getUrl(url), filter);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobExecutionDtos", hasSize(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(1)));

        assertThat(response, hasJsonPath("$.jobExecutionDtos[0].id", is(jobExecutionModel0.getId())));
    }
}
