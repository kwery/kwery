package com.kwery.tests.fluentlenium.email;

import org.junit.Test;

import static com.kwery.tests.fluentlenium.email.EmailConfigurationPage.INPUT_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class EmailConfigurationTestEmailValidationUiTest extends EmailConfigurationPageWithDataSetUp {
    @Test
    public void test() {
        page.submitEmptyTestEmailForm();
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> INPUT_VALIDATION_ERROR_MESSAGE.equals(page.testEmailToFieldValidationMessage()));
    }
}
