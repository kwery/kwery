package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobListFilterDto;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class JobApiControllerListJobsTest extends AbstractPostLoginApiTest {
    private JobLabelModel jobLabelModel;
    private JobModel jobModel0;
    private JobModel jobModel1;

    @Before
    public void setUp() {
        jobModel0 = jobModelWithoutDependents();
        jobDbSetUp(jobModel0);

        jobModel1 = jobModelWithoutDependents();
        jobDbSetUp(jobModel1);

        JobModel jobModel2 = jobModelWithoutDependents();
        jobDbSetUp(jobModel2);

        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        jobModel0.setLabels(ImmutableSet.of(jobLabelModel));
        jobJobLabelDbSetUp(jobModel0);

        jobModel1.setLabels(ImmutableSet.of(jobLabelModel));
        jobJobLabelDbSetUp(jobModel1);
    }


    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobs");

        JobListFilterDto filter = new JobListFilterDto();
        filter.setJobLabelId(jobLabelModel.getId());
        filter.setPageNo(0);
        filter.setResultCount(1);

        String response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.length()", is(1)));
        assertEquals(toJson(ImmutableList.of(new JobModelHackDto(jobModel0))), response, true);

        filter.setPageNo(1);

        response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.length()", is(1)));
        assertEquals(toJson(ImmutableList.of(new JobModelHackDto(jobModel1))), response, true);
    }
}
