package com.kwery.tests.services.mail;

import com.kwery.services.mail.MailConfigurationNotFoundException;
import com.kwery.services.mail.MailService;
import com.kwery.services.mail.MultipleSmtpConfigurationFoundException;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Test;

public class MailServiceGetSmtpConfigurationEmptyTest extends RepoDashTestBase {
    @Test(expected = MailConfigurationNotFoundException.class)
    public void test() throws MailConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        getInstance(MailService.class).getSmtpConfiguration();
    }
}
