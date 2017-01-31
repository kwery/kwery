package com.kwery.tests.fluentlenium.email;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

public class EmailConfigurationEmptyPageSetUp extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected EmailConfigurationPage page;

    @Before
    public void setUpSmtpConfigurationSaveValidationUiTest() {
        page.go();

        if (!page.isRendered()) {
            TestCase.fail("Could not render email configuration page");
        }

        page.waitForModalDisappearance();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
