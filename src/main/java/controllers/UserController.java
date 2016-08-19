package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.UserDao;
import models.User;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import views.ActionResult;

import static com.google.common.base.Optional.of;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_FAILURE;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_NEXT_ACTION;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_SUCCESS;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@Singleton
public class UserController {
    public static final String ONBOARDING_POST_ADMIN_USER_CREATION_ACTION = "#onboarding/add-datasource";

    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    public Result addAdminUser(Context context, User user) {
        User existingUser = userDao.getByUsername(user.getUsername());

        Result json = Results.json();
        ActionResult actionResult = null;

        if (existingUser == null) {
            userDao.save(user);
            String message = messages.get(ADMIN_USER_ADDITION_SUCCESS, context, of(json), user.getUsername()).get();
            String nextActionName = messages.get(ADMIN_USER_ADDITION_NEXT_ACTION, context, of(json)).get();
            actionResult = new ActionResult(success, message, nextActionName, ONBOARDING_POST_ADMIN_USER_CREATION_ACTION);
        } else {
            String message = messages.get(ADMIN_USER_ADDITION_FAILURE, context, of(json), user.getUsername()).get();
            actionResult = new ActionResult(failure, message);
        }

        return json.render(actionResult);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
