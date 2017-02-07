package com.kwery.tests.services.mail.smtp;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationNotFoundException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static junit.framework.TestCase.fail;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class SmtpServiceGetSmptConfigurationTest extends RepoDashTestBase {
    protected SmtpService smtpService;
    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        smtpConfiguration = TestUtil.smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        smtpService = getInstance(SmtpService.class);
    }

    @Test
    public void test() {
        try {
            assertThat(getInstance(SmtpService.class).getSmtpConfiguration(), theSameBeanAs(smtpConfiguration));
        } catch (SmtpConfigurationNotFoundException | MultipleSmtpConfigurationFoundException e) {
            fail();
        }
    }
}
