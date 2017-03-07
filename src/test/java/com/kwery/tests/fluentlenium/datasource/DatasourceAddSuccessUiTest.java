package com.kwery.tests.fluentlenium.datasource;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.models.Datasource;
import com.kwery.tests.util.*;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.kwery.models.Datasource.Type.*;
import static com.kwery.tests.util.Messages.ONBOARDING_REPORT_ADD_POST_DATASOURCE_M;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DatasourceAddSuccessUiTest extends ChromeFluentTest {
    protected boolean onboardingFlow;

    public DatasourceAddSuccessUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Rule
    public PostgreSqlDockerRule postgreSqlDockerRule = new PostgreSqlDockerRule();

    @Rule
    public RedshiftDockerRule redshiftDockerRule = new RedshiftDockerRule();

    @Page
    protected DatasourceAddPage page;

    @Before
    public void setUpAddDatasourceSuccessTest() {
        if (onboardingFlow) {
            System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
        }

        page.setOnboardingFlow(onboardingFlow);
        page.go();

        if (!page.isRendered()) {
            fail("Add datasource page is not rendered");
        }
    }

    @Test
    public void testAddMySqlDatasource() {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        page.submitForm(datasource);
        page.waitForModalDisappearance();

        if (onboardingFlow) {
            page.waitForReportAddPage();
            page.getActionResultComponent().assertInfoMessage(ONBOARDING_REPORT_ADD_POST_DATASOURCE_M);
        } else {
            page.waitForDatasourceListPage();
            page.waitForSuccessMessage(datasource.getLabel(), MYSQL);
        }
    }

    @Test
    public void testAddPostgreSqlDatasource() {
        Datasource datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();
        page.submitForm(datasource);
        page.waitForModalDisappearance();

        if (onboardingFlow) {
            page.waitForReportAddPage();
            page.getActionResultComponent().assertInfoMessage(ONBOARDING_REPORT_ADD_POST_DATASOURCE_M);
        } else {
            page.waitForDatasourceListPage();
            page.waitForSuccessMessage(datasource.getLabel(), POSTGRESQL);
        }
    }

    @Test
    public void testAddRedshiftDatasource() {
        Datasource datasource = redshiftDockerRule.getRedshiftDocker().datasource();
        page.submitForm(datasource);
        page.waitForModalDisappearance();

        if (onboardingFlow) {
            page.waitForReportAddPage();
            page.getActionResultComponent().assertInfoMessage(ONBOARDING_REPORT_ADD_POST_DATASOURCE_M);
        } else {
            page.waitForDatasourceListPage();
            page.waitForSuccessMessage(datasource.getLabel(), REDSHIFT);
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}

