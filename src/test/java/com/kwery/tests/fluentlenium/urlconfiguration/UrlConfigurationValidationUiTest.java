package com.kwery.tests.fluentlenium.urlconfiguration;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
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

public class UrlConfigurationValidationUiTest extends ChromeFluentTest {

    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected UrlConfigurationSavePage page;
    private DomainConfigurationDao domainConfigurationDao;

    @Before
    public void setUp() {
        page.go();
        page.waitForDefaultValues();

        domainConfigurationDao = ninjaServerRule.getInjector().getInstance(DomainConfigurationDao.class);
    }

    @Test
    public void test() {
        page.submitEmptyForm();
        page.assertEmptyFieldValidationMessages();

        UrlConfiguration urlConfiguration = domainSetting();
        urlConfiguration.setDomain("a");
        page.submitForm(urlConfiguration);

        page.assertMinLengthFieldValidationMessage(urlConfiguration.getDomain());

        urlConfiguration = domainSetting();
        urlConfiguration.setPort(UrlConfiguration.PORT_MIN - 1);

        page.submitForm(urlConfiguration);
        page.assertMinimumValueFieldValidationMessage();

        urlConfiguration = domainSetting();
        urlConfiguration.setPort(RandomUtils.nextInt(UrlConfiguration.PORT_MAX + 1, Integer.MAX_VALUE));
        page.submitForm(urlConfiguration);
        page.assertMaximumValueFieldValidationMessage();

        urlConfiguration = domainSetting();
        urlConfiguration.setDomain(RandomStringUtils.randomAlphanumeric(UrlConfiguration.DOMAIN_MAX + 1, UrlConfiguration.DOMAIN_MAX + 10));
        page.submitForm(urlConfiguration);
        page.waitForSaveSuccessMessage();

        UrlConfiguration fromDb = domainConfigurationDao.get().get(0);
        assertThat(fromDb.getDomain(), is(urlConfiguration.getDomain().substring(0, UrlConfiguration.DOMAIN_MAX)));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
