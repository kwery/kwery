package controllers;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.UserDao;
import models.User;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import views.ActionResult;
import views.ActionResult.Status;

import static com.google.common.base.Optional.of;
import static controllers.MessageKeys.ADMIN_USER_CREATION_FAILURE;
import static controllers.MessageKeys.ADMIN_USER_CREATION_SUCCESS;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@Singleton
public class UserController {
    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    public Result createAdminUser(Context context, User user) {
        User existingUser = userDao.getByUsername(user.getUsername());

        Result json = Results.json();
        ActionResult actionResult = null;

        if (existingUser == null) {
            userDao.save(user);
            String message = messages.get(ADMIN_USER_CREATION_SUCCESS, context, of(json), user.getUsername()).get();
            actionResult = new ActionResult(success, message);
        } else {
            String message = messages.get(ADMIN_USER_CREATION_FAILURE, context, of(json), user.getUsername()).get();
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
