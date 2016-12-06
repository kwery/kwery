package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobExecutionDto;
import com.kwery.models.JobExecutionModel;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.models.JobExecutionModel.Status.SUCCESS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerJobExecutionModelToJobExecutionDtoTest {
    @Test
    public void test() {
        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setExecutionStart(1481024793933l);
        jobExecutionModel.setExecutionEnd(1581024803933l);

        String executionId = UUID.randomUUID().toString();

        jobExecutionModel.setExecutionId(executionId);
        jobExecutionModel.setStatus(SUCCESS);

        JobExecutionDto dto = new JobApiController(null, null, null, null)
                .jobExecutionModelToJobExecutionDto(jobExecutionModel);

        assertThat(dto.getStatus(), is(SUCCESS.name()));
        assertThat(dto.getStart(), is("Tue Dec 06 2016 17:16"));
        assertThat(dto.getEnd(), is("Fri Feb 07 2020 03:03"));
        assertThat(dto.getExecutionId(), is(executionId));
    }
}
