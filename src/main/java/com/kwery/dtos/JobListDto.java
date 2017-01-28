package com.kwery.dtos;

import java.util.List;

public class JobListDto {
    protected int totalCount;
    protected List<JobModelHackDto> jobModelHackDtos;

    public JobListDto(int totalCount, List<JobModelHackDto> jobModelHackDtos) {
        this.totalCount = totalCount;
        this.jobModelHackDtos = jobModelHackDtos;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<JobModelHackDto> getJobModelHackDtos() {
        return jobModelHackDtos;
    }

    public void setJobModelHackDtos(List<JobModelHackDto> jobModelHackDtos) {
        this.jobModelHackDtos = jobModelHackDtos;
    }
}
