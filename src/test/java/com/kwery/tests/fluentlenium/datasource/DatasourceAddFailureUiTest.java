package com.kwery.tests.fluentlenium.datasource;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.models.Datasource;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testcontainers.containers.MySQLContainer;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static java.text.MessageFormat.format;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class DatasourceAddFailureUiTest extends ChromeFluentTest {
    protected boolean onboardingFlow;

    public DatasourceAddFailureUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true},
                {false},
        });
    }

    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    @Page
    protected DatasourceAddPage page;

    protected Datasource datasource;

    @Before
    public void setUpAddDatasourceFailureTest() {
        datasource = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
        datasource.setId(dbId());

        datasourceDbSetup(datasource);

        if (onboardingFlow) {
            System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
        }

        page.setOnboardingFlow(onboardingFlow);

        page.go();

        if (!page.isRendered()) {
            fail("Could not render update datasource page");
        }
    }

    @Test
    public void test() {
        Datasource newDatasource = datasource();
        newDatasource.setLabel(datasource.getLabel());

        String connectionFailureErrorMessage = "Failed to connect to MYSQL datasource. Communications link failure " +
                "The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server. SQL State - 08S01.";

        page.submitForm(newDatasource);
        page.waitForFailureMessage();
        assertThat(
                page.errorMessages(),
                containsInAnyOrder(
                        format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, newDatasource.getLabel()),
                        connectionFailureErrorMessage
                )
        );
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
