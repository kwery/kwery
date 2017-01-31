package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static com.kwery.tests.util.TestUtil.emailConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.smtpConfigurationDbSetUp;
import static junit.framework.TestCase.fail;

public class EmailConfigurationPageWithDataSetUp extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected EmailConfigurationPage page;

    protected SmtpConfiguration smtpConfiguration;
    protected EmailConfiguration emailConfiguration;

    @Before
    public void setUpSmtpConfigurationSaveValidationUiTest() {
        emailConfiguration = emailConfigurationDbSetUp();
        smtpConfiguration = smtpConfigurationDbSetUp();

        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render email configuration page");
        }

        page.waitForModalDisappearance();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
