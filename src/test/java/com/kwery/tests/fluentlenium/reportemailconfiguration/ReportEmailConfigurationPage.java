package com.kwery.tests.fluentlenium.reportemailconfiguration;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("#/logo/save")
public class ReportEmailConfigurationPage extends KweryFluentPage implements RepoDashPage {
    protected ActionResultComponent actionResultComponent;

    @Override
    public boolean isRendered() {
        return el(".report-email-configuration-f").displayed();
    }

    public void submitForm(String logo) {
        $(".logo-f").fill().with(logo);
        $(".save-logo-f").submit();
    }

    public ActionResultComponent getActionResultComponent() {
        return actionResultComponent;
    }
}
