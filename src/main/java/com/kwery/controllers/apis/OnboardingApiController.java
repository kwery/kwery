package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dao.UserDao;
import com.kwery.dtos.OnboardingNextActionDto;
import com.kwery.filters.DashRepoSecureFilter;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.dtos.OnboardingNextActionDto.Action.*;
import static ninja.Results.json;

@Singleton
public class OnboardingApiController {
    protected Logger logger = LoggerFactory.getLogger(OnboardingApiController.class);

    @Inject
    protected Messages messages;

    @Inject
    protected UserDao userDao;

    @Inject
    protected DatasourceDao datasourceDao;

    @Inject
    protected JobDao jobDao;

    @Inject
    protected DashRepoSecureFilter dashRepoSecureFilter;

    public Result nextAction(Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        if (!anyUserPresent()) {
            return json().render(new OnboardingNextActionDto(OnboardingNextActionDto.Action.SIGN_UP));
        } else if (!dashRepoSecureFilter.isLoggedIn(context)) {
            return json().render(new OnboardingNextActionDto(OnboardingNextActionDto.Action.LOGIN));
        } else {
            if (!doesDatasourceExist()) {
                if (logger.isTraceEnabled()) logger.trace("<");
                return json().render(new OnboardingNextActionDto(ADD_DATASOURCE));
            } else if(!doesSqlQueryExist()) {
                if (logger.isTraceEnabled()) logger.trace("<");
                return json().render(new OnboardingNextActionDto(ADD_JOB));
            } else {
                if (logger.isTraceEnabled()) logger.trace("<");
                return json().render(new OnboardingNextActionDto(SHOW_HOME_SCREEN));
            }
        }
    }

    protected boolean doesDatasourceExist() {
        //TODO - Query using count
        return datasourceDao.getAll().size() > 0;
    }

    protected boolean doesSqlQueryExist() {
        //TODO - Query using count
        return jobDao.getAllJobs().size() > 0;
    }

    protected boolean anyUserPresent() {
        return !userDao.list().isEmpty();
    }
}
