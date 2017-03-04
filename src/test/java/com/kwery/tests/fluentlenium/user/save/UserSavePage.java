package com.kwery.tests.fluentlenium.user.save;

import com.google.common.base.CaseFormat;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;

import java.util.Map;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/sign-up")
public class UserSavePage extends KweryFluentPage {
    @Wait
    protected ActionResultComponent actionResultComponent;

    public void saveUser(Map<FormField, ?> map) {
        if (map.containsKey(FormField.firstName)) {
            $(".first-name-f").withHook(WaitHook.class).fill().with(String.valueOf(map.get(FormField.firstName)));
        }

        if (map.containsKey(FormField.middleName)) {
            $(".middle-name-f").withHook(WaitHook.class).fill().with(String.valueOf(map.get(FormField.middleName)));
        }

        if (map.containsKey(FormField.lastName)) {
            $(".last-name-f").withHook(WaitHook.class).fill().with(String.valueOf(map.get(FormField.lastName)));
        }

        if (map.containsKey(FormField.email)) {
            $(".email-f").withHook(WaitHook.class).fill().with(String.valueOf(map.get(FormField.email)));
        }

        if (map.containsKey(FormField.password)) {
            $(".password-f").withHook(WaitHook.class).fill().with(String.valueOf(map.get(FormField.password)));
        }

        if (map.containsKey(FormField.confirmPassword)) {
            $(".confirm-password-f").withHook(WaitHook.class).fill().with(String.valueOf(map.get(FormField.confirmPassword)));
        }

        $(".save-f").withHook(WaitHook.class).click();
    }

    public void assertNonEmptyValidationMessage(FormField field) {
        String cls = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.name()) + "-f";
        assertThat(el("div", withClass().contains(cls), withTextContent().notContains("")));
    }

    public ActionResultComponent getActionResultComponent() {
        return actionResultComponent;
    }

}
