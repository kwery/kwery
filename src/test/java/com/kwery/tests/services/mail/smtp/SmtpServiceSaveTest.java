package com.kwery.tests.services.mail.smtp;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class SmtpServiceSaveTest extends RepoDashTestBase {
    @Test
    public void test() throws Exception {
        SmtpConfiguration details = smtpConfigurationWithoutId();
        DozerBeanMapper mapper = new DozerBeanMapper();
        SmtpConfiguration expected = mapper.map(details, SmtpConfiguration.class);
        getInstance(SmtpService.class).save(details);
        new DbTableAsserterBuilder(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(expected)).columnsToIgnore(SmtpConfiguration.COLUMN_ID).build().assertTable();
    }
}
