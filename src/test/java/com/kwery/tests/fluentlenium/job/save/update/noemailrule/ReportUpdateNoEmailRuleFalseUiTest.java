package com.kwery.tests.fluentlenium.job.save.update.noemailrule;

import org.junit.Test;

public class ReportUpdateNoEmailRuleFalseUiTest extends AbstractReportUpdateNoEmailRuleUiTest {
    boolean noEmail = false;

    @Test
    public void test() {
        page.waitForModalDisappearance();
        page.ensureEmailRuleUnchecked();
        page.submitReportSaveForm();
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();
        assertNoEmailRule(noEmail);
    }

    @Override
    public boolean isNoEmailRule() {
        return noEmail;
    }
}
