package com.kwery.tests.fluentlenium.email.warning;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.tests.fluentlenium.email.warning.EmailConfigurationNotPresentUiTest.TestScenario.senderDetails;
import static com.kwery.tests.fluentlenium.email.warning.EmailConfigurationNotPresentUiTest.TestScenario.smtpConfiguration;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SENDER_DETAILS_MISSING_M;
import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SMTP_MISSING_M;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.containingText;

@RunWith(Parameterized.class)
public class EmailConfigurationNotPresentUiTest extends ChromeFluentTest {
    protected TestScenario testScenario;

    public EmailConfigurationNotPresentUiTest(TestScenario testScenario) {
        this.testScenario = testScenario;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {smtpConfiguration},
                {senderDetails},
        });
    }

    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Before
    public void setUp() {
        if (testScenario == senderDetails) {
            SmtpConfiguration smtpConfiguration = smtpConfiguration();
            smtpConfigurationDbSetUp(smtpConfiguration);
        } else if (testScenario == smtpConfiguration) {
            EmailConfiguration emailConfiguration = emailConfiguration();
            DbUtil.emailConfigurationDbSet(emailConfiguration);
        }

        goTo("/#email/configuration");
        waitForModalDisappearance();
    }

    @Test
    public void test() {
        if (testScenario == senderDetails) {
            assertThat(el("div", containingText(EMAIL_CONFIGURATION_SENDER_DETAILS_MISSING_M))).isDisplayed();
        } else if (testScenario == smtpConfiguration) {
            assertThat(el("div", containingText(EMAIL_CONFIGURATION_SMTP_MISSING_M))).isDisplayed();
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    public enum TestScenario {
        senderDetails, smtpConfiguration
    }
}
