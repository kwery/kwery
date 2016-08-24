package controllers.modules.actionresult;

import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;

import static conf.Routes.ACTION_RESULT_COMPONENT_HTML;
import static conf.Routes.TEMPLATE_PATH;

@Singleton
public class ActionResultModuleController {
    public Result html() {
        Result html = Results.html();
        html.template("views/modules/actionresult/actionResult.html.ftl");
        return html;
    }

    public Result js() {
        Result js = Results.html();
        js.template("views/modules/actionresult/actionResult.js.ftl");
        js.render(TEMPLATE_PATH, ACTION_RESULT_COMPONENT_HTML);
        js.contentType("text/javascript");
        return js;
    }
}
