package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionDto;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.models.JobExecutionModel.Status.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerJobExecutionModelToJobExecutionDtoTest {
    protected JobApiController jobApiController = new JobApiController(null, null, null, null, null, null, null, null, null, null, null);

    protected JobModel jobModel = jobModelWithoutDependents();

    @Test
    public void testSuccess() {
        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setId(1);
        jobExecutionModel.setExecutionStart(1481024793933l);
        jobExecutionModel.setExecutionEnd(1581024803933l);

        String executionId = UUID.randomUUID().toString();

        jobExecutionModel.setExecutionId(executionId);
        jobExecutionModel.setStatus(SUCCESS);

        jobExecutionModel.setJobModel(jobModel);

        JobExecutionDto dto = jobApiController .jobExecutionModelToJobExecutionDto(jobExecutionModel);

        assertThat(dto.getId(), is(jobExecutionModel.getId()));
        assertThat(dto.getStatus(), is(SUCCESS.name()));
        assertThat(dto.getStart(), is("Tue Dec 06 2016 17:16"));
        assertThat(dto.getEnd(), is("Fri Feb 07 2020 03:03"));
        assertThat(dto.getExecutionId(), is(executionId));
        assertThat(dto.getLabel(), is(jobModel.getName()));
    }

    @Test
    public void testFailure() {
        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setId(1);
        jobExecutionModel.setExecutionStart(1481024793933l);
        jobExecutionModel.setExecutionEnd(1581024803933l);

        String executionId = UUID.randomUUID().toString();

        jobExecutionModel.setExecutionId(executionId);
        jobExecutionModel.setStatus(FAILURE);

        jobExecutionModel.setJobModel(jobModel);

        JobExecutionDto dto = jobApiController.jobExecutionModelToJobExecutionDto(jobExecutionModel);

        assertThat(dto.getId(), is(jobExecutionModel.getId()));
        assertThat(dto.getStatus(), is(FAILURE.name()));
        assertThat(dto.getStart(), is("Tue Dec 06 2016 17:16"));
        assertThat(dto.getEnd(), is("Fri Feb 07 2020 03:03"));
        assertThat(dto.getExecutionId(), is(executionId));
        assertThat(dto.getLabel(), is(jobModel.getName()));
    }

    @Test
    public void testOngoing() {
        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setId(1);
        jobExecutionModel.setExecutionStart(1481024793933l);
        jobExecutionModel.setExecutionEnd(1581024803933l);

        String executionId = UUID.randomUUID().toString();

        jobExecutionModel.setExecutionId(executionId);
        jobExecutionModel.setStatus(ONGOING);

        jobExecutionModel.setJobModel(jobModel);

        JobExecutionDto dto = jobApiController.jobExecutionModelToJobExecutionDto(jobExecutionModel);

        assertThat(dto.getId(), is(jobExecutionModel.getId()));
        assertThat(dto.getStatus(), is(ONGOING.name()));
        assertThat(dto.getStart(), is("Tue Dec 06 2016 17:16"));
        assertThat(dto.getEnd(), is(""));
        assertThat(dto.getExecutionId(), is(executionId));
        assertThat(dto.getLabel(), is(jobModel.getName()));
    }

    @Test
    public void testKilled() {
        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setId(1);
        jobExecutionModel.setExecutionStart(1481024793933l);
        jobExecutionModel.setExecutionEnd(1581024803933l);

        String executionId = UUID.randomUUID().toString();

        jobExecutionModel.setExecutionId(executionId);
        jobExecutionModel.setStatus(KILLED);

        jobExecutionModel.setJobModel(jobModel);

        JobExecutionDto dto = jobApiController.jobExecutionModelToJobExecutionDto(jobExecutionModel);

        assertThat(dto.getId(), is(jobExecutionModel.getId()));
        assertThat(dto.getStatus(), is(KILLED.name()));
        assertThat(dto.getStart(), is("Tue Dec 06 2016 17:16"));
        assertThat(dto.getEnd(), is("Fri Feb 07 2020 03:03"));
        assertThat(dto.getExecutionId(), is(executionId));
        assertThat(dto.getLabel(), is(jobModel.getName()));
    }
}
