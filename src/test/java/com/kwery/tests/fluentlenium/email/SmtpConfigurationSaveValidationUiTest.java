package com.kwery.tests.fluentlenium.email;

import com.google.common.collect.ImmutableSet;
import com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.INPUT_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.RADIO_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField.host;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField.port;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SmtpConfigurationSaveValidationUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void testNonLocalSetting() {
        page.submitEmptySmtpConfigurationForm();
        page.waitForSmtpConfigurationFormValidationMessage();
        for (SmtpConfigurationFormField field : SmtpConfigurationFormField.values()) {
            if (field == SmtpConfigurationFormField.ssl) {
                assertThat(page.validationMessage(field), is(RADIO_VALIDATION_ERROR_MESSAGE));
            } else {
                assertThat(page.validationMessage(field), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }
    }

    @Test
    public void testLocalSetting() {
        page.clickUseLocalSetting();

        page.clearHostField();
        page.clearPortField();

        page.submitEmptySmtpConfigurationForm();
        page.waitForSmtpConfigurationFormValidationMessage();

        for (SmtpConfigurationFormField field : SmtpConfigurationFormField.values()) {
            if (ImmutableSet.of(host, port).contains(field)) {
                assertThat(page.validationMessage(field), is(INPUT_VALIDATION_ERROR_MESSAGE));
            } else {
                assertThat(page.validationMessage(field), is(""));
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
