package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.tests.fluentlenium.joblabel.save.ReportLabelSavePage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report-label/{reportLabelId}")
public class ReportLabelUpdatePage extends ReportLabelSavePage {
    protected String reportLabel;

    public String getReportLabel() {
        return reportLabel;
    }

    public void setReportLabel(String reportLabel) {
        this.reportLabel = reportLabel;
    }

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".label-name-f")).attribute("value", getReportLabel());
        return true;
    }

    public int selectedParentLabelOptionIndex() {
        int i = 0;
        for (FluentWebElement option : $(".parent-label-f option")) {
            if (option.selected()) {
                return i;
            }
            i = i + 1;
        }

        throw new AssertionError();
    }

    public String selectedParentLabelOptionText() {
        int i = 0;
        for (FluentWebElement option : $(".parent-label-f option")) {
            if (option.selected()) {
                return option.text();
            }
            i = i + 1;
        }

        throw new AssertionError();
    }
}
