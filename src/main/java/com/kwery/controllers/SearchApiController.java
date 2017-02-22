package com.kwery.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.search.JobSearchDao;
import com.kwery.dao.search.SearchFilter;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.JobModel;
import ninja.Result;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ninja.Results.json;

@Singleton
public class SearchApiController {
    protected JobSearchDao jobSearchDao;

    @Inject
    public SearchApiController(JobSearchDao jobSearchDao) {
        this.jobSearchDao = jobSearchDao;
    }

    public Result searchJobs(SearchFilter searchFilter) {
        List<JobModel> jobs = jobSearchDao.search(searchFilter);
        List<JobModelHackDto> jobModelHackDtos = jobs.stream().map(jobModel -> new JobModelHackDto(jobModel, jobModel.getParentJob())).collect(toList());
        return json().render(jobModelHackDtos);
    }
}
