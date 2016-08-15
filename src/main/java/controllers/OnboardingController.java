package controllers;

import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;

@Singleton
public class OnboardingController {
    public Result welcome() {
        Result html = Results.html();
        html.template("views/components/onboarding/welcome.html.ftl");
        return html;
    }

    public Result createAdminUserHtml() {
        Result html = Results.html();
        html.template("views/components/onboarding/createAdminUser.html.ftl");
        return html;
    }

    public Result createAdminUserJs() {
        Result js = Results.html();
        js.template("views/components/onboarding/createAdminUser.js.ftl");
        js.contentType("text/javascript");
        return js;
    }
}
