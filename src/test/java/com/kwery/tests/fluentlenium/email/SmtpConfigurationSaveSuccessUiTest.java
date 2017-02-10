package com.kwery.tests.fluentlenium.email;

import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import org.junit.Test;

import static com.kwery.models.SmtpConfiguration.COLUMN_ID;
import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_ADDED_M;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class SmtpConfigurationSaveSuccessUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() throws Exception {
        SmtpConfiguration config = smtpConfigurationWithoutId();
        config.setUseLocalSetting(false);

        page.submitSmtpConfigurationForm(config);
        page.waitForSaveMessage(SMTP_CONFIGURATION_ADDED_M);

        new DbTableAsserterBuilder(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(config)).columnsToIgnore(COLUMN_ID).build().assertTable();
    }
}
