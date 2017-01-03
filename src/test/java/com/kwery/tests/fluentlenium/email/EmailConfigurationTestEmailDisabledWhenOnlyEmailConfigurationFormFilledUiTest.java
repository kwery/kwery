package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import org.junit.Test;

import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SAVED_M;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailConfigurationTestEmailDisabledWhenOnlyEmailConfigurationFormFilledUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        EmailConfiguration cofig = emailConfiguration();
        page.submitEmailConfigurationForm(cofig);
        page.waitForSaveMessage(EMAIL_CONFIGURATION_SAVED_M);

        assertThat(page.isTestEmailConfigurationToFieldDisabled(), is(true));
        assertThat(page.isTestEmailConfigurationSubmitButtonDisabled(), is(true));
    }
}
