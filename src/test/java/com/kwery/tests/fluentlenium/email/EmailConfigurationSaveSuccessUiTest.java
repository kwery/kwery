package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import org.junit.Test;

import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SAVED_M;
import static com.kwery.tests.util.TestUtil.emailConfigurationWithoutId;

public class EmailConfigurationSaveSuccessUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        EmailConfiguration config = emailConfigurationWithoutId();
        page.submitEmailConfigurationForm(config);
        page.waitForSaveMessage(EMAIL_CONFIGURATION_SAVED_M);
    }
}
