package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import org.junit.Test;

import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SAVED_M;
import static com.kwery.tests.util.Messages.EMAIL_TEST_SUCCESS_M;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_ADDED_M;
import static com.kwery.tests.util.TestUtil.emailConfigurationWithoutId;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class EmailConfigurationTestEmailConfigurationSuccessUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        SmtpConfiguration smtpConfiguration = smtpConfigurationWithoutId();
        page.submitSmtpConfigurationForm(smtpConfiguration);
        page.waitForSaveMessage(SMTP_CONFIGURATION_ADDED_M);

        EmailConfiguration emailConfiguration = emailConfigurationWithoutId();
        page.submitEmailConfigurationForm(emailConfiguration);
        page.waitForSaveMessage(EMAIL_CONFIGURATION_SAVED_M);

        page.submitTestEmailForm("foo@goo.com");
        page.waitForSaveMessage(EMAIL_TEST_SUCCESS_M);
    }
}