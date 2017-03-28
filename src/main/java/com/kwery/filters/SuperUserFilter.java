package com.kwery.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.views.ActionResult;
import ninja.*;

import static com.kwery.controllers.apis.UserApiController.SESSION_USERNAME_KEY;

@Singleton
public class SuperUserFilter implements Filter {
    protected final UserDao userDao;

    @Inject
    public SuperUserFilter(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        ActionResult actionResult = actionResult(context);

        if (actionResult == null) {
            return filterChain.next(context);
        } else {
            return Results.json().render(actionResult);
        }
    }

    protected String email(Context context) {
        if (context.getSession() != null) {
            return context.getSession().get(SESSION_USERNAME_KEY);
        }
        return "";
    }

    public ActionResult actionResult(Context context) {
        String email = email(context);
        User user = userDao.getUserByEmail(email);

        if (user.getSuperUser() == null || !user.getSuperUser()) {
            ActionResult actionResult = new ActionResult();
            actionResult.setStatus(ActionResult.Status.failure);
            return actionResult;
        }

        return null;
    }
}
