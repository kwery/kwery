package filters;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import views.ActionResult;

import static controllers.MessageKeys.USER_NOT_LOGGED_IN;
import static controllers.apis.UserApiController.SESSION_USERNAME_KEY;

public class DashRepoSecureFilter implements Filter {
    public static final String LOGIN_JS_VIEW = "views/modules/user/login/login.js.ftl";
    @Inject
    private Messages messages;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        if (!isLoggedIn(context)) {
            Result result = Results.json();
            String msg = messages.get(USER_NOT_LOGGED_IN, context, Optional.of(result)).get();
            ActionResult actionResult = new ActionResult(ActionResult.Status.failure, msg);
            return result.render(actionResult);
        }
        return filterChain.next(context);
    }

    private boolean isLoggedIn(Context context) {
        return !(context.getSession() == null || context.getSession().get(SESSION_USERNAME_KEY) == null);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
