package com.kwery.tests.fluentlenium.user.superuseredit;

import com.google.common.base.CaseFormat;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/{userId}/edit")
public class SuperUserEditPage extends KweryFluentPage {
    protected ActionResultComponent actionResultComponent;

    public void resetPassword(String password, String confirmPassword) {
        $(".password-f").withHook(WaitHook.class).fill().with(password);
        $(".confirm-password-f").withHook(WaitHook.class).fill().with(confirmPassword);
        $(".password-reset-f").click();
    }

    public ActionResultComponent getActionResultComponent() {
        return actionResultComponent;
    }

    public void assertNonEmptyValidationMessage(FormField field) {
        String cls = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.name()) + "-f";
        assertThat(el("div", withClass().contains(cls), withTextContent().notContains("")));
    }

    public void saveSuperUser(boolean superUser) {
        if (el(".super-user-f").withHook(WaitHook.class).selected()) {
            if (!superUser) {
             el(".super-user-f").click();
            }
        } else {
            if (superUser) {
                el(".super-user-f").click();
            }
        }
        $(".promote-to-admin-f").click();
    }

    public void assertSuperUserState(boolean superUser) {
        if (superUser) {
            assertThat(el("input", withClass().contains("super-user-f"))).isSelected();
        } else {
            assertThat(el("input", withClass().contains("super-user-f"))).isNotSelected();
        }
    }

    public enum FormField {
        password, confirmPassword
    }
}
