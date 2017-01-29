package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.util.TestUtil.emailConfigurationDbSetUp;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailConfigurationTestEmailDisabledWhenSmtpConfigurationEmptyUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected EmailConfigurationPage page;

    protected EmailConfiguration emailConfiguration;

    @Before
    public void setUpSmtpConfigurationSaveValidationUiTest() {
        emailConfiguration = emailConfigurationDbSetUp();

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
}
