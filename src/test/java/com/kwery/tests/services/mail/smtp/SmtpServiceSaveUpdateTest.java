package com.kwery.tests.services.mail.smtp;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;

public class SmtpServiceSaveUpdateTest extends RepoDashTestBase {
    protected SmtpService smtpService;
    private SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpSmtpServiceSaveUpdateTest() {
        smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);
        smtpService = getInstance(SmtpService.class);
    }

    @Test
    public void test() throws Exception {
        SmtpConfiguration updated = TestUtil.smtpConfigurationWithoutId();
        updated.setId(smtpConfiguration.getId());

        DozerBeanMapper mapper = new DozerBeanMapper();
        SmtpConfiguration expected = mapper.map(updated, SmtpConfiguration.class);

        smtpService.save(updated);
        new DbTableAsserter.DbTableAsserterBuilder(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(expected)).build().assertTable();
    }
}
