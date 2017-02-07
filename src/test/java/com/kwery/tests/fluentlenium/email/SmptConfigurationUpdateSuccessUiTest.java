package com.kwery.tests.fluentlenium.email;

import com.kwery.models.SmtpConfiguration;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_UPDATED_M;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class SmptConfigurationUpdateSuccessUiTest extends EmailConfigurationPageWithDataSetUp {
    @Test
    public void test() {
        if (smtpConfiguration.isUseLocalSetting()) {
            page.clickUseLocalSetting();
        }

        SmtpConfiguration updated = smtpConfigurationWithoutId();
        updated.setUseLocalSetting(false);

        page.submitSmtpConfigurationForm(updated);
        page.waitForModalDisappearance();
        page.waitForSaveMessage(SMTP_CONFIGURATION_UPDATED_M);

        updated.setId(smtpConfiguration.getId());

        assertThat(smtpConfigurationDao.get(), hasSize(1));
        assertThat(smtpConfigurationDao.get().get(0), theSameBeanAs(updated));
    }
}
