package com.kwery.tests.fluentlenium.job.save.update.noemailrule;

import org.junit.Test;

public class ReportUpdateNoEmailRuleToggleToTrueUiTest extends AbstractReportUpdateNoEmailRuleUiTest {
    boolean noEmail = false;

    @Test
    public void test() {
        page.ensureEmailRuleChecked();
        page.submitReportSaveForm();
        page.waitForReportSaveSuccessMessage();
        assertNoEmailRule(!noEmail);
    }

    @Override
    public boolean isNoEmailRule() {
        return noEmail;
    }
}
