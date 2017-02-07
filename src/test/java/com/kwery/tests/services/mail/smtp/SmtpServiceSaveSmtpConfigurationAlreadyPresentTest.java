package com.kwery.tests.services.mail.smtp;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;

public class SmtpServiceSaveSmtpConfigurationAlreadyPresentTest extends RepoDashDaoTestBase {
    protected SmtpService smtpService;
    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpSmtpServiceSaveSmtpConfigurationAlreadyPresentTest() throws Exception {
        smtpConfiguration = smtpConfiguration();
        smtpConfigurationTable(smtpConfiguration);
        smtpService = getInstance(SmtpService.class);
    }

    @Test(expected = SmtpConfigurationAlreadyPresentException.class)
    public void testNullId() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpConfiguration.setId(null);
        smtpService.save(smtpConfiguration);
    }

    @Test(expected = SmtpConfigurationAlreadyPresentException.class)
    public void testNonNullId() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpConfiguration.setId(2);
        smtpService.save(smtpConfiguration);
    }
}
