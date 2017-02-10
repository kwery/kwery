package com.kwery.tests.fluentlenium.email;

import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static junit.framework.TestCase.fail;

public class EmailConfigurationPageWithDataSetUp extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected EmailConfigurationPage page;

    protected SmtpConfiguration smtpConfiguration;
    protected EmailConfiguration emailConfiguration;

    protected SmtpConfigurationDao smtpConfigurationDao;

    @Before
    public void setUpSmtpConfigurationSaveValidationUiTest() {
        emailConfiguration = emailConfiguration();
        emailConfigurationDbSet(emailConfiguration);

        smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render email configuration page");
        }

        smtpConfigurationDao = ninjaServerRule.getInjector().getInstance(SmtpConfigurationDao.class);

        page.waitForModalDisappearance();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
