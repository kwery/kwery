package com.kwery.tests.fluentlenium.email;

import com.google.common.collect.ImmutableSet;
import com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField.host;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField.port;

public class SmtpConfigurationSaveValidationUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void testNonLocalSetting() {
        page.submitEmptySmtpConfigurationForm();
        page.assertNonEmptySmtpConfigurationFormValidationMessage();
        for (SmtpConfigurationFormField field : SmtpConfigurationFormField.values()) {
            page.assertNonEmptyValidationMessage(field);
        }
    }

    @Test
    public void testLocalSetting() {
        page.clickUseLocalSetting();

        page.clearHostField();
        page.clearPortField();

        page.submitEmptySmtpConfigurationForm();
        page.assertNonEmptySmtpConfigurationFormValidationMessage();

        for (SmtpConfigurationFormField field : SmtpConfigurationFormField.values()) {
            if (ImmutableSet.of(host, port).contains(field)) {
                page.assertNonEmptyValidationMessage(field);
            } else {
                page.assertEmptyValidationMessage(field);
            }
        }
    }

    @Test
    public void testToggle() {
        testNonLocalSetting();
        testLocalSetting();
        page.clickUseLocalSetting();
        testNonLocalSetting();
    }
}
