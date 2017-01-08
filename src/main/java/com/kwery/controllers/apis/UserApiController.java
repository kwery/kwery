package com.kwery.controllers.apis;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.UserDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.User;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.validation.FieldViolation;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kwery.views.ActionResult;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Optional.of;
import static com.kwery.controllers.ControllerUtil.fieldMessages;
import static com.kwery.controllers.MessageKeys.ADMIN_USER_ADDITION_FAILURE;
import static com.kwery.controllers.MessageKeys.ADMIN_USER_ADDITION_SUCCESS;
import static com.kwery.controllers.MessageKeys.LOGIN_FAILURE;
import static com.kwery.controllers.MessageKeys.LOGIN_SUCCESS;
import static com.kwery.controllers.MessageKeys.USER_DELETE_SUCCESS;
import static com.kwery.controllers.MessageKeys.USER_DELETE_YOURSELF;
import static com.kwery.controllers.MessageKeys.USER_UPDATE_SUCCESS;
import static ninja.Results.json;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;

@Singleton
public class UserApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SESSION_USERNAME_KEY = "username";

    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    @FilterWith(DashRepoSecureFilter.class)
    public Result addAdminUser(Context context, @JSR303Validation User user, Validation validation) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("User payload - " + user);

        Result json = json();
        ActionResult actionResult = null;

        if (validation.hasViolations()) {
            actionResult = new ActionResult(failure, fieldMessages(validation, context, messages, json));

            List<String> violations = new LinkedList<>();
            violations.add("Field violations:");

            for (FieldViolation fieldViolation : validation.getFieldViolations()) {
                violations.add(fieldViolation.constraintViolation.getMessageKey());
            }

            logger.error(Joiner.on("").join(violations));
        } else {
            boolean isUpdate = false;

            if (user.getId() != null && user.getId() > 0) {
                isUpdate = true;
            }

            if (isUpdate) {
                logger.info("Updating user with payload - " + user);
            } else {
                logger.info("Adding user with payload - " + user);
            }

            User existingUser = userDao.getByUsername(user.getUsername());

            //TODO - Check whether user name is being changed
            if (isUpdate) {
                userDao.update(user);
                String message = messages.get(USER_UPDATE_SUCCESS, context, of(json), user.getUsername()).get();
                actionResult = new ActionResult(success, message);
            } else {
                if (existingUser == null) {
                    userDao.save(user);
                    String message = messages.get(ADMIN_USER_ADDITION_SUCCESS, context, of(json), user.getUsername()).get();
                    actionResult = new ActionResult(success, message);
                } else {
                    logger.error("User already exists with username - {}", existingUser.getUsername());
                    String message = messages.get(ADMIN_USER_ADDITION_FAILURE, context, of(json), user.getUsername()).get();
                    actionResult = new ActionResult(failure, message);
                }
            }
        }

        if (logger.isTraceEnabled()) logger.trace("<");

        return json.render(actionResult);
    }

    public Result login(Context context, User user) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("User logging in - {}", user);

        User fromDb = userDao.getUser(user.getUsername(), user.getPassword());

        Result json = json();
        ActionResult actionResult = null;

        if (fromDb == null) {
            logger.error("User with username {} not found", user.getUsername());
            String message = messages.get(LOGIN_FAILURE, context, of(json)).get();
            actionResult = new ActionResult(failure, message);
        } else {
            String message = messages.get(LOGIN_SUCCESS, context, of(json), user.getUsername()).get();
            actionResult = new ActionResult(success, message);
            context.getSession().put(SESSION_USERNAME_KEY, user.getUsername());
        }

        if (logger.isTraceEnabled()) logger.trace("<");

        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result logout(Context context) {
        //TODO needs refinement
        if (logger.isTraceEnabled()) logger.trace(">");

        String loggedInUserName = context.getSession().get(SESSION_USERNAME_KEY);
        logger.info("{} logging out", loggedInUserName);

        context.getSession().clear();

        if (logger.isTraceEnabled()) logger.trace("<");
        return json();
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result userById(@PathParam("userId") int userId) {
        if (logger.isTraceEnabled()) logger.trace(">");

        User user = userDao.getById(userId);

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(user);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result user(Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        String loggedInUserName = context.getSession().get(SESSION_USERNAME_KEY);
        User user = userDao.getByUsername(loggedInUserName);
        Result json = json();
        json.render(user);

        if (logger.isTraceEnabled()) logger.trace("<");
        return json;
    }

    public Result delete(@PathParam("userId") int userId, Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("Deleting user - {}", userId);

        String deletedUsername = userDao.getById(userId).getUsername();
        ActionResult actionResult = null;
        Result json = json();
        if (deletedUsername.equals(context.getSession().get(SESSION_USERNAME_KEY))) {
            logger.error("{} user is trying to delete himself", userId);

            String message = messages.get(USER_DELETE_YOURSELF, context, of(json)).get();
            actionResult = new ActionResult(failure, message);
        } else {
            userDao.delete(userId);
            String message = messages.get(USER_DELETE_SUCCESS, context, of(json), deletedUsername).get();
            actionResult = new ActionResult(success, message);
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result list() {
        if (logger.isTraceEnabled()) logger.trace(">");

        List<User> list = userDao.list();
        //Masking password
        list.forEach(u -> {u.setPassword("");});

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(list);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
