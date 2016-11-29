package com.kwery.tests.fluentlenium.email;

import com.kwery.tests.fluentlenium.email.EmailConfigurationPage.SmtpConfigurationFormField;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.INPUT_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.RADIO_VALIDATION_ERROR_MESSAGE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SmtpConfigurationSaveValidationUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
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
}
