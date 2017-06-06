package com.kwery.controllers.apis;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.conf.TemplateDirectory;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.views.ActionResult;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.uploads.DiskFileItemProvider;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;
import org.apache.commons.io.Charsets;

import java.io.File;
import java.io.IOException;

import static ninja.Results.html;
import static ninja.Results.json;

@Singleton
public class CustomTemplateApiController {
    protected final TemplateDirectory templateDirectory;
    protected final JobDao jobDao;

    @Inject
    public CustomTemplateApiController(TemplateDirectory templateDirectory, JobDao jobDao) {
        this.templateDirectory = templateDirectory;
        this.jobDao = jobDao;
    }

    @FileProvider(DiskFileItemProvider.class)
    public Result saveCustomTemplate(@Param("templateFile") FileItem fileItem, @PathParam("jobId") int reportId) {
        File file = templateDirectory.save(fileItem.getFile());

        JobModel jobModel = jobDao.getJobById(reportId);

        String existing = jobModel.getTemplate();

        jobModel.setTemplate(file.getName());

        jobDao.save(jobModel);

        //Delete existing file only if the current operation succeeds
        templateDirectory.delete(existing);

        //return json().render("{\"status\":\"success\",\"messages\":[\"\"],\"fieldMessages\":null}");
        return html().renderRaw("{\"status\":\"success\",\"messages\":[\"\"],\"fieldMessages\":null}");
    }

    public Result getCustomTemplateContent(@PathParam("jobId") int reportId) throws IOException {
        JobModel jobModel = jobDao.getJobById(reportId);

        String templateContent = "";
        if (!"".equals(jobModel.getTemplate())) {
            File templateFile = templateDirectory.getTemplate(jobModel.getTemplate());
            templateContent = Files.toString(templateFile, Charsets.UTF_8);
        }

        return json().render(ImmutableMap.of(
                "content", templateContent
        ));
    }

    public Result deleteCustomTemplate(@PathParam("jobId") int reportId) {
        JobModel jobModel = jobDao.getJobById(reportId);
        String template = jobModel.getTemplate();

        jobModel.setTemplate("");
        jobDao.save(jobModel);

        templateDirectory.delete(template);

        return Results.json().render(new ActionResult(ActionResult.Status.success, ""));
    }
}
