package com.kwery.tests.fluentlenium.email;

import org.junit.Test;

public class SmtpConfigurationLocalSettingDefaultFieldsUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        page.clickUseLocalSetting();
        page.assertLocalSmtpDefaultValues();
    }
}
