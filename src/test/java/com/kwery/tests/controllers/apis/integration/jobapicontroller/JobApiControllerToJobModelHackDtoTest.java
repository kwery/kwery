package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class JobApiControllerToJobModelHackDtoTest extends RepoDashTestBase {
    private JobApiController jobApiController;

    private JobModel jobModel;
    private JobModel parentJob;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        parentJob = jobModelWithoutDependents();
        parentJob.setCronExpression("");
        jobDbSetUp(parentJob);

        parentJob.getChildJobs().add(jobModel);
        jobDependentDbSetUp(parentJob);

        jobModel.setParentJob(parentJob);

        JobExecutionModel jobExecutionModel = TestUtil.jobExecutionModel();
        jobExecutionModel.setExecutionStart(1487139110071l); //Wed Feb 15 11:41:50 IST 2017
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);

        JobExecutionModel parentJobExecutionModel = TestUtil.jobExecutionModel();
        parentJobExecutionModel.setJobModel(parentJob);
        parentJobExecutionModel.setExecutionStart(1487139595283l); //Wed Feb 15 11:49:55 IST 2017
        jobExecutionDbSetUp(parentJobExecutionModel);

        jobApiController = getInstance(JobApiController.class);
    }

    @Test
    public void test0() {
        JobModelHackDto expected = new JobModelHackDto(jobModel, parentJob);
        expected.setLastExecution("Wed Feb 15 2017 11:41");

        JobModelHackDto actual = jobApiController.toJobModelHackDto(jobModel);

        assertThat(actual, theSameBeanAs(expected).excludeProperty("nextExecution"));
        assertThat(actual.getNextExecution(), not(isEmptyOrNullString()));
    }

    @Test
    public void test1() {
        JobModelHackDto expected = new JobModelHackDto(parentJob, null);
        expected.setLastExecution("Wed Feb 15 2017 11:49");

        JobModelHackDto actual = jobApiController.toJobModelHackDto(parentJob);

        assertThat(actual, theSameBeanAs(expected).excludeProperty("nextExecution"));
        assertThat(actual.getNextExecution(), isEmptyString());
    }
}
