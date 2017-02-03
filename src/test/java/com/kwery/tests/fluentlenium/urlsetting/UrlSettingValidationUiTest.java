package com.kwery.tests.fluentlenium.urlsetting;

import com.kwery.dao.DomainSettingDao;
import com.kwery.models.UrlSetting;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.util.TestUtil.domainSetting;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class UrlSettingValidationUiTest extends ChromeFluentTest {

    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected UrlSettingSavePage page;
    private DomainSettingDao domainSettingDao;

    @Before
    public void setUp() {
        page.go();
        page.waitForDefaultValues();

        domainSettingDao = ninjaServerRule.getInjector().getInstance(DomainSettingDao.class);
    }

    @Test
    public void test() {
        page.submitEmptyForm();
        page.assertEmptyFieldValidationMessages();

        UrlSetting urlSetting = domainSetting();
        urlSetting.setDomain("a");
        page.submitForm(urlSetting);

        page.assertMinLengthFieldValidationMessage(urlSetting.getDomain());

        urlSetting = domainSetting();
        urlSetting.setPort(UrlSetting.PORT_MIN - 1);

        page.submitForm(urlSetting);
        page.assertMinimumValueFieldValidationMessage();

        urlSetting = domainSetting();
        urlSetting.setPort(RandomUtils.nextInt(UrlSetting.PORT_MAX + 1, Integer.MAX_VALUE));
        page.submitForm(urlSetting);
        page.assertMaximumValueFieldValidationMessage();

        urlSetting = domainSetting();
        urlSetting.setDomain(RandomStringUtils.randomAlphanumeric(UrlSetting.DOMAIN_MAX + 1, UrlSetting.DOMAIN_MAX + 10));
        page.submitForm(urlSetting);
        page.waitForSaveSuccessMessage();

        UrlSetting fromDb = domainSettingDao.get().get(0);
        assertThat(fromDb.getDomain(), is(urlSetting.getDomain().substring(0, UrlSetting.DOMAIN_MAX)));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
