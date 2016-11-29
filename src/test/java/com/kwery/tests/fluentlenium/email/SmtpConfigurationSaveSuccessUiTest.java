package com.kwery.tests.fluentlenium.email;

import com.kwery.models.SmtpConfiguration;
import org.junit.Test;

import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_ADDED_M;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class SmtpConfigurationSaveSuccessUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        SmtpConfiguration config = smtpConfigurationWithoutId();
        page.submitSmtpConfigurationForm(config);
        page.waitForSaveMessage(SMTP_CONFIGURATION_ADDED_M);
    }
}
