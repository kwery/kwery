package com.kwery.tests.fluentlenium.email;

import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.TestUtil;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailConfigurationTestEmailDisabledWhenOnlySmtpConfigurationFormFilledUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        SmtpConfiguration smtpConfiguration = TestUtil.smtpConfigurationWithoutId();
        page.submitSmtpConfigurationForm(smtpConfiguration);
        page.waitForSaveMessage(Messages.SMTP_CONFIGURATION_ADDED_M);

        assertThat(page.isTestEmailConfigurationToFieldDisabled(), is(true));
        assertThat(page.isTestEmailConfigurationSubmitButtonDisabled(), is(true));
    }
}
