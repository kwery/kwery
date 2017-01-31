package com.kwery.tests.fluentlenium.email;

import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import org.junit.Test;

import static com.kwery.models.SmtpConfiguration.COLUMN_ID;
import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_ADDED_M;

public class SmtpConfigurationUseLocalSettingSaveSuccessUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() throws Exception {
        page.clickUseLocalSetting();
        page.clickSmtpFormSubmit();
        page.waitForSaveMessage(SMTP_CONFIGURATION_ADDED_M);

        SmtpConfiguration expected = new SmtpConfiguration();
        expected.setHost("localhost");
        expected.setPort(25);
        expected.setUseLocalSetting(true);

        new DbTableAsserterBuilder(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(expected)).columnsToIgnore(COLUMN_ID).build().assertTable();
    }
}
