package com.kwery.tests.services.mail;

import com.kwery.services.mail.SmtpConfigurationNotFoundException;
import com.kwery.services.mail.SmtpService;
import com.kwery.services.mail.MultipleSmtpConfigurationFoundException;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Test;

public class SmtpServiceGetSmtpConfigurationNotFoundTest extends RepoDashTestBase {
    @Test(expected = SmtpConfigurationNotFoundException.class)
    public void test() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        getInstance(SmtpService.class).getSmtpConfiguration();
    }
}
