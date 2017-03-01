package com.kwery.tests.fluentlenium.email;

import org.junit.Test;

public class EmailConfigurationTestEmailValidationUiTest extends EmailConfigurationPageWithDataSetUp {
    @Test
    public void test() {
        page.submitEmptyTestEmailForm();
        page.assertNonEmptyEmailToFieldValidationMessage();
    }
}
