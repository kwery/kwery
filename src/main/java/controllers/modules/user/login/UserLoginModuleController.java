package controllers.modules.user.login;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static conf.Routes.API_PATH;
import static conf.Routes.LOGIN_API;
import static conf.Routes.LOGIN_COMPONENT_HTML;
import static conf.Routes.TEMPLATE_PATH;
import static controllers.MessageKeys.LOGIN;
import static controllers.MessageKeys.PASSWORD;
import static controllers.MessageKeys.USER_NAME;
import static filters.DashRepoSecureFilter.LOGIN_JS_VIEW;

@Singleton
public class UserLoginModuleController {

    @Inject
    private Messages messages;

    public Result html(Context context) {
        Result html = Results.html();

        String usernameButtonText = messages.get(USER_NAME, context, Optional.of(html)).get();
        html.render("usernameButtonM", usernameButtonText);

        String passwordButtonText = messages.get(PASSWORD, context, Optional.of(html)).get();
        html.render("passwordButtonM", passwordButtonText);

        String loginButtonText = messages.get(LOGIN, context, Optional.of(html)).get();
        html.render("loginButtonM", loginButtonText);

        html.template("views/modules/user/login/login.html.ftl");
        return html;
    }

    public Result js(Context context) {
        Result js = Results.html();
        js.template(LOGIN_JS_VIEW);

        js.render(TEMPLATE_PATH, LOGIN_COMPONENT_HTML);
        js.render(API_PATH, LOGIN_API);

        js.contentType("text/javascript");

        return js;
    }
}
