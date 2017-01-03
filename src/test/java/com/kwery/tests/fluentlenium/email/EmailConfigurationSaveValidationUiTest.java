package com.kwery.tests.fluentlenium.email;

import com.kwery.tests.fluentlenium.email.EmailConfigurationPage.EmailConfigurationFormField;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.EmailConfigurationFormField.bcc;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.EmailConfigurationFormField.replyTo;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.INPUT_VALIDATION_ERROR_MESSAGE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailConfigurationSaveValidationUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        page.submitEmptyEmailConfigurationForm();
        page.waitForEmailConfigurationFormValidationMessage();

        for (EmailConfigurationFormField field : EmailConfigurationFormField.values()) {
            if ((field == bcc) || (field == replyTo)) {
                assertThat(page.validationMessage(field), is(""));
            } else {
                assertThat(page.validationMessage(field), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }
    }
}
