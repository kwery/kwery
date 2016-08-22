package controllers.apis;

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
import static controllers.MessageKeys.LOGIN_FAILURE;
import static controllers.MessageKeys.LOGIN_SUCCESS;
import static controllers.modules.user.addadmin.UserAddAdminModuleController.ONBOARDING_POST_ADMIN_USER_CREATION_ACTION;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@Singleton
public class UserApiController {
    public static final String SESSION_USERNAME_KEY = "username";

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

            context.getSession().put(SESSION_USERNAME_KEY, user.getUsername());

            String message = messages.get(ADMIN_USER_ADDITION_SUCCESS, context, of(json), user.getUsername()).get();
            String nextActionName = messages.get(ADMIN_USER_ADDITION_NEXT_ACTION, context, of(json)).get();
            actionResult = new ActionResult(success, message, nextActionName, ONBOARDING_POST_ADMIN_USER_CREATION_ACTION);
        } else {
            String message = messages.get(ADMIN_USER_ADDITION_FAILURE, context, of(json), user.getUsername()).get();
            actionResult = new ActionResult(failure, message);
        }

        return json.render(actionResult);
    }

    public Result login(Context context, User user) {
        User fromDb = userDao.getUser(user.getUsername(), user.getPassword());

        Result json = Results.json();
        ActionResult actionResult = null;

        if (fromDb == null) {
            String message = messages.get(LOGIN_FAILURE, context,  of(json)).get();
            actionResult = new ActionResult(failure, message);
        } else {
            String message = messages.get(LOGIN_SUCCESS, context, of(json), user.getUsername()).get();
            actionResult = new ActionResult(success, message);
            context.getSession().put(SESSION_USERNAME_KEY, user.getUsername());
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
