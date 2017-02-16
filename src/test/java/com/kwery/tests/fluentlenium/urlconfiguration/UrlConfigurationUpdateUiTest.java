package com.kwery.tests.fluentlenium.urlconfiguration;

import com.kwery.models.UrlConfiguration;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.UrlConfiguration.URL_CONFIGURATION_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationTable;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static org.junit.rules.RuleChain.outerRule;

public class UrlConfigurationUpdateUiTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected UrlConfigurationSavePage page;

    private UrlConfiguration setting;

    @Before
    public void setUp() {
        setting = TestUtil.domainSetting();
        domainConfigurationDbSetUp(setting);
        page.go();
        page.waitForModalDisappearance();
        page.waitForFormValues(setting);
    }

    @Test
    public void test() throws Exception {
        page.clickSubmit();
        page.waitForSaveSuccessMessage();
        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainConfigurationTable(setting)).build().assertTable();

        UrlConfiguration updated = domainSettingWithoutId();
        updated.setId(setting.getId());

        page.submitForm(updated);

        page.waitForSaveSuccessMessage();
        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainConfigurationTable(updated)).build().assertTable();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
