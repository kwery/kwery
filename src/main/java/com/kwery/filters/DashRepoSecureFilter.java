package com.kwery.filters;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.views.ActionResult;
import ninja.*;
import ninja.i18n.Messages;

import static com.kwery.controllers.MessageKeys.USER_NOT_LOGGED_IN;
import static com.kwery.controllers.apis.UserApiController.SESSION_USERNAME_KEY;

@Singleton
public class DashRepoSecureFilter implements Filter {
    @Inject
    private Messages messages;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        Result result = Results.json();
        ActionResult actionResult = actionResult(context, result);
        if (actionResult == null) {
            return filterChain.next(context);
        } else {
            return result.render(actionResult);
        }
    }

    public ActionResult actionResult(Context context, Result result) {
        ActionResult actionResult = null;
        if (!isLoggedIn(context)) {
            String msg = messages.get(USER_NOT_LOGGED_IN, context, Optional.of(result)).get();
            actionResult = new ActionResult(ActionResult.Status.failure, msg);
        }
        return actionResult;
    }

    public boolean isLoggedIn(Context context) {
        return !(context.getSession() == null || context.getSession().get(SESSION_USERNAME_KEY) == null);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
