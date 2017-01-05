package com.kwery.dtos;

import java.util.List;

public class JobExecutionListDto {
    protected List<JobExecutionDto> jobExecutionDtos;
    protected long totalCount;

    public List<JobExecutionDto> getJobExecutionDtos() {
        return jobExecutionDtos;
    }

    public void setJobExecutionDtos(List<JobExecutionDto> jobExecutionDtos) {
        this.jobExecutionDtos = jobExecutionDtos;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
