package filters;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import controllers.MessageKeys;
import controllers.apis.UserApiController;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import views.ActionResult;

public class DashRepoSecureFilter implements Filter {
    @Inject
    private Messages messages;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        if (context.getSession() == null || context.getSession().get(UserApiController.SESSION_USERNAME_KEY) == null) {
            Result result = Results.json();
            String msg = messages.get(MessageKeys.USER_NOT_LOGGED_IN, context, Optional.of(result)).get();
            ActionResult actionResult = new ActionResult(ActionResult.Status.failure, msg);
            return result.render(actionResult);
        }
        return filterChain.next(context);
    }
}
