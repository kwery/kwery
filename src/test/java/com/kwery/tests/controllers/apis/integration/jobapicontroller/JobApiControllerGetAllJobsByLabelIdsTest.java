package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.controllers.apis.JobApiController;
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

public class JobApiControllerGetAllJobsByLabelIdsTest extends AbstractPostLoginApiTest {
    JobModel jobModel0;
    JobModel dependentJobModel;
    private JobLabelModel jobLabelModel0;

    @Before
    public void setUp() {
        jobModel0 = jobModelWithoutDependents();
        dependentJobModel = jobModelWithoutDependents();

        jobDbSetUp(ImmutableList.of(jobModel0, dependentJobModel));

        jobModel0.getChildJobs().add(dependentJobModel);
        jobDependentDbSetUp(jobModel0);

        dependentJobModel.setParentJob(jobModel0);

        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobModel0.setLabels(ImmutableSet.of(jobLabelModel0));
        jobJobLabelDbSetUp(jobModel0);

        JobModel jobModel1 = jobModelWithoutDependents();
        jobDbSetUp(jobModel1);

        JobLabelModel jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        jobModel1.setLabels(ImmutableSet.of(jobLabelModel1));
        jobJobLabelDbSetUp(jobModel1);
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobs");
        String response = ninjaTestBrowser.postJson(getUrl(url), ImmutableMap.of("jobLabelId", jobLabelModel0.getId()));
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.length()", is(1)));
        assertEquals(toJson(ImmutableList.of(new JobModelHackDto(jobModel0))), response, true);
    }
}
