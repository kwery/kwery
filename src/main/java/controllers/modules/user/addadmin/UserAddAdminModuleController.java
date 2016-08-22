package controllers.modules.user.addadmin;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static conf.Routes.ADD_ADMIN_USER_API;
import static conf.Routes.ADD_ADMIN_USER_HTML;
import static conf.Routes.API_PATH;
import static conf.Routes.TEMPLATE_PATH;
import static controllers.MessageKeys.CREATE;
import static controllers.MessageKeys.PASSWORD;
import static controllers.MessageKeys.USER_NAME;

@Singleton
public class UserAddAdminModuleController {
    public static final String ONBOARDING_POST_ADMIN_USER_CREATION_ACTION = "#onboarding/add-datasource";

    @Inject
    private Messages messages;

    public Result html(Context context) {
        Result html = Results.html();

        String usernameButtonText = messages.get(USER_NAME, context, Optional.of(html)).get();
        html.render("usernameButtonM", usernameButtonText);

        String passwordButtonText = messages.get(PASSWORD, context, Optional.of(html)).get();
        html.render("passwordButtonM", passwordButtonText);

        String createButtonText = messages.get(CREATE, context, Optional.of(html)).get();
        html.render("createButtonM", createButtonText);

        html.template("views/modules/user/addadmin/addAdminUser.html.ftl");
        return html;
    }

    public Result js() {
        Result js = Results.html();
        js.template("views/modules/user/addadmin/addAdminUser.js.ftl");
        js.render(TEMPLATE_PATH, ADD_ADMIN_USER_HTML);
        js.render(API_PATH, ADD_ADMIN_USER_API);
        js.contentType("text/javascript");
        return js;
    }
}
