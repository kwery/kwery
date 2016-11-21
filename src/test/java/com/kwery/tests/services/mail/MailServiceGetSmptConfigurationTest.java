package com.kwery.tests.services.mail;

import com.kwery.services.mail.MailConfigurationNotFoundException;
import com.kwery.services.mail.MailService;
import com.kwery.tests.dao.smtpdetaildao.SmtpDetailDaoGetTest;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class MailServiceGetSmptConfigurationTest extends SmtpDetailDaoGetTest {
    @Test
    public void test() {
        try {
            assertThat(getInstance(MailService.class).getSmtpConfiguration(), theSameBeanAs(smtpDetail));
        } catch (MailConfigurationNotFoundException e) {
            fail();
        }
    }
}
