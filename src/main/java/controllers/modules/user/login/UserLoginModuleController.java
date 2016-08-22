package controllers.modules.user.login;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static controllers.MessageKeys.LOGIN;
import static controllers.MessageKeys.PASSWORD;
import static controllers.MessageKeys.USER_NAME;

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
}
