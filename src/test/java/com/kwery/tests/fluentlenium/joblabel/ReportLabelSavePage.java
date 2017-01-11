package com.kwery.tests.fluentlenium.joblabel;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;

import java.text.MessageFormat;

import static com.kwery.tests.util.Messages.REPORT_LABEL_SAVE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

public class ReportLabelSavePage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".label-form-f").isDisplayed();
        return true;
    }

    @Override
    public String getUrl() {
        return "/#report-label/add";
    }

    public void fillAndSubmitForm(String label, Integer parentLabelIndex) {
        fill(".label-name-f").with(label);
        if (parentLabelIndex != null) {
            find(className("parent-label-opted-f")).click();
            findFirst(className("parent-label-f")).fillSelect().withIndex(parentLabelIndex);
        }
        find(className("submit-f")).click();
    }

    public void waitForLabelSaveSuccessMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(MessageFormat.format(REPORT_LABEL_SAVE_SUCCESS_M, label));
    }
}
