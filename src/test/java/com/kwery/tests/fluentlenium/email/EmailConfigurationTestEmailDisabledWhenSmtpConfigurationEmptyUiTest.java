package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailConfigurationTestEmailDisabledWhenSmtpConfigurationEmptyUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected EmailConfigurationPage page;

    protected EmailConfiguration emailConfiguration;

    @Before
    public void setUpSmtpConfigurationSaveValidationUiTest() {
        emailConfiguration = emailConfiguration();
        emailConfigurationDbSet(emailConfiguration);

        page = newInstance(EmailConfigurationPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render email configuration page");
        }
    }

    @Test
    public void test() {
        assertThat(page.isTestEmailConfigurationSubmitButtonDisabled(), is(true));
        assertThat(page.isTestEmailConfigurationToFieldDisabled(), is(true));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
