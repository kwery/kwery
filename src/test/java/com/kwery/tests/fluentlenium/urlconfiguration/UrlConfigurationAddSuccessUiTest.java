package com.kwery.tests.fluentlenium.urlconfiguration;

import com.google.common.primitives.Ints;
import com.kwery.models.UrlConfiguration;
import com.kwery.models.UrlConfiguration.Scheme;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.UrlConfiguration.URL_CONFIGURATION_TABLE;
import static com.kwery.models.UrlConfiguration.ID_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static org.junit.rules.RuleChain.outerRule;

public class UrlConfigurationAddSuccessUiTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected UrlConfigurationSavePage page;

    @Before
    public void setUp() {
        page.go();
        page.waitForDefaultValues();
    }

    @Test
    public void testDefaultSave() throws Exception {
        page.clickSubmit();
        page.waitForSaveSuccessMessage();

        UrlConfiguration setting = new UrlConfiguration();
        setting.setScheme(Scheme.http);
        setting.setDomain("localhost");
        setting.setPort(Ints.tryParse(page.getPort()));

        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainSettingTable(setting)).columnsToIgnore(ID_COLUMN).build().assertTable();
    }

    @Test
    public void test() throws Exception {
        UrlConfiguration urlConfiguration = domainSettingWithoutId();
        page.submitForm(urlConfiguration);
        page.waitForSaveSuccessMessage();
        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainSettingTable(urlConfiguration)).columnsToIgnore(ID_COLUMN).build().assertTable();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
