package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.UserDao;
import com.kwery.dtos.OnboardingNextActionDto;
import com.kwery.models.User;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kwery.views.ActionResult;

import static com.google.common.base.Optional.of;
import static com.kwery.controllers.MessageKeys.ONBOARDING_ROOT_USER_CREATED;
import static com.kwery.dtos.OnboardingNextActionDto.Action.ADD_ADMIN_USER;
import static com.kwery.dtos.OnboardingNextActionDto.Action.ADD_DATASOURCE;
import static com.kwery.dtos.OnboardingNextActionDto.Action.ADD_SQL_QUERY;
import static com.kwery.dtos.OnboardingNextActionDto.Action.SHOW_EXECUTING_QUERIES;
import static ninja.Results.json;
import static com.kwery.views.ActionResult.Status.success;

@Singleton
public class OnboardingApiController {
    protected Logger logger = LoggerFactory.getLogger(OnboardingApiController.class);

    public static final String ROOT_USERNAME = "root";
    public static final String ROOT_PASSWORD = "foobarmoo";

    @Inject
    protected Messages messages;

    @Inject
    protected UserDao userDao;

    @Inject
    protected DatasourceDao datasourceDao;

    @Inject
    protected SqlQueryDao sqlQueryDao;

    public Result addRootUser(Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        Result json = json();
        ActionResult actionResult = null;

        //TODO - Take care of else case
        if (!doesRootUserExist()) {
            User root = new User();
            root.setUsername(ROOT_USERNAME);
            root.setPassword(ROOT_PASSWORD);
            userDao.save(root);

            String message = messages.get(ONBOARDING_ROOT_USER_CREATED, context, of(json), root.getUsername(), root.getPassword()).get();
            actionResult = new ActionResult(success, message);
        } else {
            logger.error("{} user exists, hence not adding", ROOT_USERNAME);
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json.render(actionResult);
    }

    public Result nextAction() {
        if (logger.isTraceEnabled()) logger.trace(">");

        if (!doesRootUserExist()) {
            if (logger.isTraceEnabled()) logger.trace("<");
            return json().render(new OnboardingNextActionDto(ADD_ADMIN_USER));
        } else if (!doesDatasourceExist()) {
            if (logger.isTraceEnabled()) logger.trace("<");
            return json().render(new OnboardingNextActionDto(ADD_DATASOURCE));
        } else if(!doesSqlQueryExist()) {
            if (logger.isTraceEnabled()) logger.trace("<");
            return json().render(new OnboardingNextActionDto(ADD_SQL_QUERY));
        } else {
            if (logger.isTraceEnabled()) logger.trace("<");
            return json().render(new OnboardingNextActionDto(SHOW_EXECUTING_QUERIES));
        }
    }

    protected boolean doesRootUserExist() {
        return userDao.getByUsername(ROOT_USERNAME) != null;
    }

    protected boolean doesDatasourceExist() {
        //TODO - Query using count
        return datasourceDao.getAll().size() > 0;
    }

    protected boolean doesSqlQueryExist() {
        //TODO - Query using count
        return sqlQueryDao.getAll().size() > 0;
    }
}
