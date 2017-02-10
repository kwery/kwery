package com.kwery.tests.services.mail.smtp;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;

public class SmtpServiceSaveMultipleSmtpConfigurationPresentTest extends RepoDashTestBase {
    protected SmtpService smtpService;

    @Before
    public void setUpSmtpServiceSaveMultipleSmtpConfigurationPresentTest() {
        for (int i = 1; i < 3; ++i) {
            smtpConfigurationDbSetUp(smtpConfiguration());
        }

        smtpService = getInstance(SmtpService.class);
    }

    @Test(expected = MultipleSmtpConfigurationFoundException.class)
    public void test() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpService.save(new SmtpConfiguration());
    }
}
