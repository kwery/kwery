package controllers.modules.datasource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import conf.Routes;
import filters.DashRepoSecureFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static com.google.common.base.Optional.of;
import static conf.Routes.API_PATH;
import static conf.Routes.TEMPLATE_PATH;
import static controllers.MessageKeys.CREATE;
import static controllers.MessageKeys.LABEL;
import static controllers.MessageKeys.PASSWORD;
import static controllers.MessageKeys.URL;
import static controllers.MessageKeys.USER_NAME;


@Singleton
public class DatasourceAddModuleController {
    @Inject
    private Messages messages;

    @FilterWith(DashRepoSecureFilter.class)
    public Result html(Context context) {
        Result html = Results.html();

        String usernameButtonText = messages.get(USER_NAME, context, of(html)).get();
        html.render("usernameButtonM", usernameButtonText);

        String passwordButtonText = messages.get(PASSWORD, context, of(html)).get();
        html.render("passwordButtonM", passwordButtonText);

        String urlButtonText = messages.get(URL, context, of(html)).get();
        html.render("urlButtonM", urlButtonText);

        String labelButtonText = messages.get(LABEL, context, of(html)).get();
        html.render("labelButtonM", labelButtonText);

        String createButtonText = messages.get(CREATE, context, of(html)).get();
        html.render("createButtonM", createButtonText);

        html.template("views/modules/datasource/addDatasource.html.ftl");

        return html;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result js() {
        Result js = Results.html();
        js.template("views/modules/datasource/addDatasource.js.ftl");

        js.render(TEMPLATE_PATH, Routes.ADD_DATASOURCE_HTML);
        js.render(API_PATH, Routes.ADD_DATASOURCE_API);

        js.contentType("text/javascript");
        return js;
    }
}
