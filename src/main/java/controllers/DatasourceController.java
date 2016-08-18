package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import conf.Routes;
import dao.DatasourceDao;
import models.Datasource;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import views.ActionResult;

import static com.google.common.base.Optional.of;
import static controllers.MessageKeys.*;
import static models.Datasource.Type.MYSQL;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;


@Singleton
public class DatasourceController {
    @Inject
    private Messages messages;

    @Inject
    private DatasourceDao datasourceDao;

    public Result addDatasourceHtml(Context context) {
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

        html.template("views/components/onboarding/addDatasource.html.ftl");
        return html;
    }

    public Result addDatasourceJs() {
        Result js = Results.html();
        js.template("views/components/onboarding/addDatasource.js.ftl");

        js.render("componentPath", Routes.ONBOARDING_ADD_DATASOURCE_HTML);
        js.render("apiPath", Routes.ONBOARDING_ADD_DATASOURCE);

        js.contentType("text/javascript");
        return js;
    }

    public Result addDatasource(Datasource datasource, Context context) {
        Datasource existingDatasource = datasourceDao.getByLabel(datasource.getLabel());

        Result json = Results.json();
        ActionResult actionResult = null;

        if (existingDatasource == null) {
            datasourceDao.save(datasource);
            String msg = messages.get(DATASOURCE_ADDITION_SUCCESS, context, of(json), MYSQL.name(), datasource.getLabel()).get();
            actionResult = new ActionResult(success, msg);
        } else {
            String msg = messages.get(DATASOURCE_ADDITION_FAILURE, context, of(json), MYSQL.name(), datasource.getLabel(), datasource.getLabel()).get();
            actionResult = new ActionResult(failure, msg);
        }

        return json.render(actionResult);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }
}
