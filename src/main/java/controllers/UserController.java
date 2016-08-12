package controllers;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import conf.Routes;
import dao.UserDao;
import models.User;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;

@Singleton
public class UserController {
    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    public Result createAdminUserGet() {
        return Results.html();
    }

    public Result createAdminUserPost(@Param("username") String username, @Param("password") String password, Context context) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userDao.save(user);

        Result result = Results.html();
        String successMessage = messages.get("admin.user.creation.success", context, Optional.of(result), user.getUsername()).get();
        context.getFlashScope().success(successMessage);

        return result;
    }
}
