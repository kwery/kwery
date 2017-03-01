package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableList;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionDto;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.JobExecutionModel.Status.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobExecutionDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class JobApiControllerExecutingJobsTest extends AbstractPostLoginApiTest {
    protected JobApiController controller = new JobApiController(null, null, null, null, null, null, null, null, null, null, null);
    private JobExecutionModel jobExecutionModel0;
    private JobExecutionModel jobExecutionModel1;

    @Before
    public void setUpJobApiControllerExecutingJobsTest() {
        /*
            1482836967412 - Tue Dec 27 16:39:27 IST 2016
            1482836933006 - Tue Dec 27 16:38:53 IST 2016
         */
        JobModel jobModel = jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);

        jobExecutionModel0 = jobExecutionModel();
        jobExecutionModel0.setExecutionStart(1482836967412l);
        jobExecutionModel0.setJobModel(jobModel);
        jobExecutionModel0.setStatus(ONGOING);
        jobExecutionDbSetUp(jobExecutionModel0);

        jobExecutionModel1 = jobExecutionModel();
        jobExecutionModel1.setExecutionStart(1482836933006l);
        jobExecutionModel1.setJobModel(jobModel);
        jobExecutionModel1.setStatus(ONGOING);
        jobExecutionDbSetUp(jobExecutionModel1);

        for (JobExecutionModel.Status status : ImmutableList.of(SUCCESS, FAILURE, KILLED)) {
            JobExecutionModel jobExecutionModel = jobExecutionModel();
            jobExecutionModel.setJobModel(jobModel);
            jobExecutionModel.setStatus(status);
            jobExecutionDbSetUp(jobExecutionModel);
        }
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listExecutingJobs");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());

        List<JobExecutionDto> expected = ImmutableList.of(jobExecutionModel0, jobExecutionModel1).stream().map(controller::jobExecutionModelToJobExecutionDto).collect(toList());

        assertThat(response, sameJSONAs(toJson(expected)));
    }
}
