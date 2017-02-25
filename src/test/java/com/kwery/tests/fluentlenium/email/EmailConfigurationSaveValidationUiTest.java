package com.kwery.tests.fluentlenium.email;

import com.kwery.tests.fluentlenium.email.EmailConfigurationPage.EmailConfigurationFormField;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.EmailConfigurationFormField.bcc;
import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.EmailConfigurationFormField.replyTo;

public class EmailConfigurationSaveValidationUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        page.submitEmptyEmailConfigurationForm();
        page.assertNonEmptyEmailConfigurationFormValidationMessage();

        for (EmailConfigurationFormField field : EmailConfigurationFormField.values()) {
            if ((field == bcc) || (field == replyTo)) {
                page.assertEmptyValidationMessage(field);
            } else {
                page.assertNonEmptyValidationMessage(field);
            }
        }
    }
}
