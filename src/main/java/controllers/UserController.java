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

@Singleton
public class UserController {
    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    public Result createAdminUser(User user, Context context) {
        User existingUser = userDao.getByUsername(user.getUsername());

        Result json = Results.json();
        ActionResult actionResult = null;

        if (existingUser == null) {
            userDao.save(user);
            String message = messages.get("admin.user.creation.success", context, Optional.of(json),
                    user.getUsername()).get();
            actionResult = new ActionResult(message, true);
        } else {
            String message = messages.get("admin.user.creation.failure", context, Optional.of(json),
                    user.getUsername()).get();
            actionResult = new ActionResult(message, false);
        }

        return json.render(actionResult);
    }
}
