package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.UserDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.User;
import com.kwery.views.ActionResult;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Optional.of;
import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static ninja.Results.json;

@Singleton
public class UserApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SESSION_USERNAME_KEY = "username";

    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    @Inject
    private DashRepoSecureFilter dashRepoSecureFilter;
    public Result login(Context context, User user) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("User logging in - {}", user);

        User fromDb = userDao.getUser(user.getEmail(), user.getPassword());

        Result json = json();
        ActionResult actionResult = null;

        if (fromDb == null) {
            logger.error("User with email {} not found", user.getEmail());
            String message = messages.get(LOGIN_FAILURE, context, of(json)).get();
            actionResult = new ActionResult(failure, message);
        } else {
            String message = messages.get(LOGIN_SUCCESS, context, of(json)).get();
            actionResult = new ActionResult(success, message);
            context.getSession().put(SESSION_USERNAME_KEY, user.getEmail());
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
        User user = userDao.getUserByEmail(loggedInUserName);
        Result json = json();
        json.render(user);

        if (logger.isTraceEnabled()) logger.trace("<");
        return json;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result delete(@PathParam("userId") int userId, Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("Deleting user - {}", userId);

        String deletedEmail = userDao.getById(userId).getEmail();
        ActionResult actionResult = null;
        Result json = json();
        if (deletedEmail.equals(context.getSession().get(SESSION_USERNAME_KEY))) {
            logger.error("{} user is trying to delete himself", userId);

            String message = messages.get(USER_DELETE_YOURSELF, context, of(json)).get();
            actionResult = new ActionResult(failure, message);
        } else {
            userDao.delete(userId);
            String message = messages.get(USER_DELETE_SUCCESS, context, of(json), deletedEmail).get();
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

    //Since this method is used by both sign up as well as user edit we cannot mark this as secure, we do the security
    //check first thing inside the method.
    public Result signUp(User user, Context context) {
        ActionResult actionResult = null;
        Result json = json();

        if (user.getId() != null && user.getId() > 0) {
            actionResult = dashRepoSecureFilter.actionResult(context, json);
        }

        if (actionResult == null) {
            User existing = userDao.getUserByEmail(user.getEmail());

            if (existing != null) {
                if (user.getId() != null && user.getId() > 0 && !existing.getId().equals(user.getId())) {
                    actionResult = new ActionResult(failure, "");
                } else {
                    actionResult = new ActionResult(failure, "");
                }
            }

            if (actionResult == null) {
                if (user.getId() != null && user.getId() > 0) {
                    userDao.update(user);
                } else {
                    userDao.save(user);
                }

                actionResult = new ActionResult(success, "");
            }
        }

        return json().render(actionResult);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
