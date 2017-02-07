package com.kwery.tests.fluentlenium.email;

import com.kwery.models.SmtpConfiguration;
import org.junit.Test;

import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_UPDATED_M;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class SmptConfigurationUpdateSuccessUiTest extends EmailConfigurationPageWithDataSetUp {
    @Test
    public void test() {
        SmtpConfiguration smtpConfiguration = smtpConfigurationWithoutId();
        smtpConfiguration.setUseLocalSetting(false);

        page.submitSmtpConfigurationForm(smtpConfiguration);
        page.waitForSaveMessage(SMTP_CONFIGURATION_UPDATED_M);
    }
}
