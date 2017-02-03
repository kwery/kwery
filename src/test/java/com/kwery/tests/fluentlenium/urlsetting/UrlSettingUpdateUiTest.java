package com.kwery.tests.fluentlenium.urlsetting;

import com.kwery.models.DomainSetting;
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

import static com.kwery.models.DomainSetting.DOMAIN_SETTING_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static org.junit.rules.RuleChain.outerRule;

public class UrlSettingUpdateUiTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected UrlSettingSavePage page;

    private DomainSetting setting;

    @Before
    public void setUp() {
        setting = TestUtil.domainSetting();
        domainSettingDbSetUp(setting);
        page.go();
        page.waitForFormValues(setting);
    }

    @Test
    public void test() throws Exception {
        page.clickSubmit();
        page.waitForSaveSuccessMessage();
        new DbTableAsserterBuilder(DOMAIN_SETTING_TABLE, domainSettingTable(setting)).build().assertTable();

        DomainSetting updated = domainSettingWithoutId();
        updated.setId(setting.getId());

        page.submitForm(updated);

        page.waitForSaveSuccessMessage();
        new DbTableAsserterBuilder(DOMAIN_SETTING_TABLE, domainSettingTable(updated)).build().assertTable();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
