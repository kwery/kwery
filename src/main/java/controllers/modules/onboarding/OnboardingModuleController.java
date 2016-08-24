package controllers.modules.onboarding;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import controllers.MessageKeys;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static controllers.MessageKeys.ONBOARDING_WELCOME;

@Singleton
public class OnboardingModuleController {
    @Inject
    private Messages messages;

    public Result html(Context context) {
        Result html = Results.html();

        String onboardingWelcome = messages.get(ONBOARDING_WELCOME, context, Optional.of(html)).get();
        html.render("onboardingWelcomeM", onboardingWelcome);

        String createAdminUserButtonText = messages.get(MessageKeys.CREATE_ADMIN_USER, context, Optional.of(html)).get();
        html.render("createAdminUserButtonM", createAdminUserButtonText);

        html.template("views/modules/onboarding/welcome.html.ftl");
        return html;
    }
}
