package com.kwery.tests.services.mail.smtp;

import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationNotFoundException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;

public class SmtpServiceGetSmtpConfigurationMultipleConfigurationTest extends RepoDashTestBase {
    @Before
    public void setUpSmtpDetailDaoGetTest() {
        for (int i = 1; i < 3; ++i) {
            smtpConfigurationDbSetUp(smtpConfiguration());
        }
    }

    @Test(expected = MultipleSmtpConfigurationFoundException.class)
    public void test() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        getInstance(SmtpService.class).getSmtpConfiguration();
    }
}
