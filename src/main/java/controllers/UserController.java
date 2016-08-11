package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.UserDao;
import models.User;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

@Singleton
public class UserController {
    @Inject
    private UserDao userDao;

    public Result createAdminUserGet() {
        return Results.html();
    }

    public Result createAdminUserPost(@Param("username") String username, @Param("password") String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userDao.save(user);

        return Results.html();
    }
}
