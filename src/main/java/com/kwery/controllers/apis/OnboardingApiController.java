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
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kwery.dtos.OnboardingNextActionDto.Action.*;
import static ninja.Results.json;

@Singleton
public class OnboardingApiController {
    public static final String TEST_ONBOARDING_SYSTEM_KEY = "testOnboarding";
    public static final String TEST_ONBOARDING_VALUE = "true";

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

    @Inject
    protected NinjaProperties ninjaProperties;

    public Result nextAction(Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        //We do not want onboarding actions to kick in during non onboarding test cases
        //If this is test, onboarding flow should kick in only during onboarding tests
        if ((ninjaProperties.isTest() && TEST_ONBOARDING_VALUE.equals(System.getProperty(TEST_ONBOARDING_SYSTEM_KEY)) || !ninjaProperties.isTest())) {
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
        } else {
            return json().render(new OnboardingNextActionDto(SHOW_HOME_SCREEN));
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
