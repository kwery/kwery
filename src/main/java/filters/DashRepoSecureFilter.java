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

import static conf.Routes.API_PATH;
import static conf.Routes.API_REQUEST_PREFIX;
import static conf.Routes.LOGIN_API;
import static conf.Routes.LOGIN_COMPONENT_HTML;
import static conf.Routes.MODULE_REQUEST_PREFIX;
import static conf.Routes.TEMPLATE_PATH;
import static controllers.MessageKeys.USER_NOT_LOGGED_IN;
import static controllers.apis.UserApiController.SESSION_USERNAME_KEY;

public class DashRepoSecureFilter implements Filter {
    public static final String LOGIN_JS_VIEW = "views/modules/user/login/login.js.ftl";
    @Inject
    private Messages messages;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        if (!isLoggedIn(context)) {
            if (isModuleRequest(context)) {
                Result js = Results.html();
                js.template(LOGIN_JS_VIEW);

                js.render(TEMPLATE_PATH, LOGIN_COMPONENT_HTML);
                js.render(API_PATH, LOGIN_API);

                js.contentType("text/javascript");

                return js;
            } else if (isApiRequest(context)) {
                Result result = Results.json();
                String msg = messages.get(USER_NOT_LOGGED_IN, context, Optional.of(result)).get();
                ActionResult actionResult = new ActionResult(ActionResult.Status.failure, msg);
                return result.render(actionResult);
            } else {
                throw new AssertionError("Unknown request path - " + context.getRequestPath());
            }
        }
        return filterChain.next(context);
    }

    private boolean isLoggedIn(Context context) {
        return !(context.getSession() == null || context.getSession().get(SESSION_USERNAME_KEY) == null);
    }

    private boolean isModuleRequest(Context context) {
        return context.getRequestPath().startsWith(MODULE_REQUEST_PREFIX);
    }

    private boolean isApiRequest(Context context) {
        return context.getRequestPath().startsWith(API_REQUEST_PREFIX);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
