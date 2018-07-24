package com.kwery.tests.services.mail.smtp;

import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationNotFoundException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Test;

public class SmtpServiceGetSmtpConfigurationNotFoundTest extends RepoDashTestBase {
    @Test(expected = SmtpConfigurationNotFoundException.class)
    public void test() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        getInstance(SmtpService.class).getSmtpConfiguration();
    }
}
