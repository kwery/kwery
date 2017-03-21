package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.UserDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.filters.SuperUserFilter;
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
import java.util.stream.Collectors;

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

    @FilterWith({DashRepoSecureFilter.class, SuperUserFilter.class})
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

    @FilterWith({DashRepoSecureFilter.class, SuperUserFilter.class})
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
    //TODO - Security, we have to do super user check here also
    public Result signUp(User user, Context context) {
        ActionResult actionResult = null;
        Result json = json();

        boolean update = user.getId() != null && user.getId() > 0;

        if (update) {
            //Login check
            actionResult = dashRepoSecureFilter.actionResult(context, json);
            if (actionResult != null) {
                return json().render(actionResult);
            }
        }

        User existing = userDao.getUserByEmail(user.getEmail());
        if (!update) { //Sign up
            if (existing != null) {//A user already exists with this email
                actionResult = new ActionResult(failure, "");
            }
        } else { //Edit
            if (existing != null && !user.getId().equals(existing.getId())) {//A user already exists with this email
                actionResult = new ActionResult(failure, "");
            }
        }

        if (actionResult == null) {
            if (update) {
                //We do not want to be in a state where there are no super users
                if (isDemotion(user) && !canDemote(user)) {
                    actionResult = new ActionResult(failure, "");
                } else {
                    //Set super user details
                    //If the FE has supplied super user details, use that, else get it from DB
                    user.setSuperUser(user.getSuperUser() != null ? user.getSuperUser() : userDao.getById(user.getId()).getSuperUser());
                    userDao.update(user);
                    actionResult = new ActionResult(success, "");
                }
            } else {
                //This is the first user, hence make this user super user by default
                if (userDao.list().isEmpty()) {
                    user.setSuperUser(true);
                }

                userDao.save(user);
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

    protected boolean isDemotion(User user) {
        if (user.getSuperUser() != null) {
            User fromDb = userDao.getById(user.getId());
            if (fromDb.getSuperUser() != null && fromDb.getSuperUser() && !user.getSuperUser()) {
                return true;
            }
        }

        return false;
    }

    protected boolean canDemote(User user) {
        List<User> superUsers = userDao.list().stream().filter(User::getSuperUser).collect(Collectors.toList());
        if (superUsers.size() > 1) {
            return true;
        } else {
            if (superUsers.isEmpty()) {
                throw new AssertionError("No super users found");
            }

            if (superUsers.get(0).getId().equals(user.getId())) {
                return false;
            } else {
                return true;
            }
        }
    }
}
