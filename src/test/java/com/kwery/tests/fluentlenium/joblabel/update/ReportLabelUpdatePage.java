package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.tests.fluentlenium.joblabel.save.ReportLabelSavePage;
import org.fluentlenium.core.domain.FluentWebElement;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ReportLabelUpdatePage extends ReportLabelSavePage {
    protected int reportLabelId;
    protected String reportLabel;

    @Override
    public String getUrl() {
        return "/#report-label/" + getReportLabelId();
    }

    public int getReportLabelId() {
        return reportLabelId;
    }

    public void setReportLabelId(int reportLabelId) {
        this.reportLabelId = reportLabelId;
    }

    public String getReportLabel() {
        return reportLabel;
    }

    public void setReportLabel(String reportLabel) {
        this.reportLabel = reportLabel;
    }

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".label-name-f").hasAttribute("value", getReportLabel());
        return true;
    }

    public int selectedParentLabelOptionIndex() {
        int i = 0;
        for (FluentWebElement option : $(".parent-label-f option")) {
            if (option.isSelected()) {
                return i;
            }
            i = i + 1;
        }

        throw new AssertionError();
    }

    public String selectedParentLabelOptionText() {
        int i = 0;
        for (FluentWebElement option : $(".parent-label-f option")) {
            if (option.isSelected()) {
                return option.getText();
            }
            i = i + 1;
        }

        throw new AssertionError();
    }
}
