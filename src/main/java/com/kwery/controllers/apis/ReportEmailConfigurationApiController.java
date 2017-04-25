package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.kwery.dao.ReportEmailConfigurationDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.ReportEmailConfigurationModel;
import com.kwery.views.ActionResult;
import ninja.FilterWith;
import ninja.Result;

import static com.kwery.views.ActionResult.Status.success;
import static ninja.Results.json;

public class ReportEmailConfigurationApiController {
    protected final ReportEmailConfigurationDao reportEmailConfigurationDao;

    @Inject
    public ReportEmailConfigurationApiController(ReportEmailConfigurationDao reportEmailConfigurationDao) {
        this.reportEmailConfigurationDao = reportEmailConfigurationDao;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveReportEmailConfiguration(ReportEmailConfigurationModel m) {
        if (m.getLogoUrl().trim().equals("")) {
            m.setLogoUrl(null);
        }
        reportEmailConfigurationDao.save(m);
        return json().render(new ActionResult(success, ""));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getReportEmailConfiguration() {
        return json().render(reportEmailConfigurationDao.get());
    }
}
