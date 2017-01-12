package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.JobLabelDao;
import com.kwery.dtos.JobLabelDto;
import com.kwery.dtos.JobLabelModelHackDto;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.JobLabelModel;
import com.kwery.views.ActionResult;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.kwery.views.ActionResult.Status.success;
import static java.util.stream.Collectors.toList;
import static ninja.Results.json;

@Singleton
public class JobLabelApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final JobLabelDao jobLabelDao;

    @Inject
    public JobLabelApiController(JobLabelDao jobLabelDao) {
        this.jobLabelDao = jobLabelDao;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveJobLabel(JobLabelDto dto) {
        if (logger.isTraceEnabled()) logger.trace("<");

        JobLabelModel model = new JobLabelModel();
        model.setLabel(dto.getLabelName());
        if (dto.getLabelId() > 0) {
            model.setId(dto.getLabelId());
        }
        if (dto.getParentLabelId() > 0) {
            model.setParentLabel(jobLabelDao.getJobLabelModelById(dto.getParentLabelId()));
        }
        jobLabelDao.save(model);

        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(new ActionResult(success, ""));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getAllJobLabels() {
        if (logger.isTraceEnabled()) logger.trace("<");
        List<JobLabelModel> all = jobLabelDao.getAllJobLabelModels();
        List<JobLabelModelHackDto> dtos = all.stream().map(jobLabelModel -> new JobLabelModelHackDto(jobLabelModel, jobLabelModel.getParentLabel())).collect(toList());
        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(dtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getJobLabelById(@PathParam("jobLabelId") int jobLabelId) {
        if (logger.isTraceEnabled()) logger.trace("<");
        JobLabelModel m = jobLabelDao.getJobLabelModelById(jobLabelId);
        JobLabelModelHackDto d = new JobLabelModelHackDto(m, m.getParentLabel());
        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(d);
    }
}
