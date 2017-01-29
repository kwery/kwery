package com.kwery.tests.fluentlenium.email;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

public class EmailConfigurationEmptyPageSetUp extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected EmailConfigurationPage page;

    @Before
    public void setUpSmtpConfigurationSaveValidationUiTest() {
        page = newInstance(EmailConfigurationPage.class);
        page.goTo(page);

        if (!page.isRendered()) {
            TestCase.fail("Could not render email configuration page");
        }

        page.waitForModalDisappearance();
    }
}
