package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import org.junit.Test;

import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.emailConfigurationWithoutId;

public class EmailConfigurationTestEmailConfigurationSuccessUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        SmtpConfiguration smtpConfiguration = wiserRule.smtpConfiguration();

        page.submitSmtpConfigurationForm(smtpConfiguration);
        page.waitForSaveMessage(SMTP_CONFIGURATION_ADDED_M);
        page.waitForModalDisappearance();

        EmailConfiguration emailConfiguration = emailConfigurationWithoutId();
        page.submitEmailConfigurationForm(emailConfiguration);
        page.waitForModalDisappearance();
        page.waitForSaveMessage(EMAIL_CONFIGURATION_SAVED_M);

        page.submitTestEmailForm("foo@goo.com");
        page.waitForModalDisappearance();
        page.waitForSaveMessage(EMAIL_TEST_SUCCESS_M);
    }
}
