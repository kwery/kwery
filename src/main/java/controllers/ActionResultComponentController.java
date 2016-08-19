package controllers;

import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;

@Singleton
public class ActionResultComponentController {
    public Result actionResultComponentHtml() {
        Result html = Results.html();
        html.template("views/components/actionresultcomponent/actionResultComponent.html.ftl");
        return html;
    }

    public Result actionResultComponentJs() {
        Result js = Results.html();
        js.template("views/components/actionresultcomponent/actionResultComponent.js.ftl");
        js.contentType("text/javascript");
        return js;
    }

    public Result actionResultDialogComponentHtml() {
        Result html = Results.html();
        html.template("views/components/actionresultdialogcomponent/actionResultDialogComponent.html.ftl");
        return html;
    }

    public Result actionResultDialogComponentJs() {
        Result js = Results.html();
        js.template("views/components/actionresultdialogcomponent/actionResultDialogComponent.js.ftl");
        js.contentType("text/javascript");
        return js;
    }
}
