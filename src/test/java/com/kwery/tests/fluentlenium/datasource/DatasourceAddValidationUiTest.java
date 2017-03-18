package com.kwery.tests.fluentlenium.datasource;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.tests.fluentlenium.datasource.DatasourceAddPage.FormField;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
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
import static com.kwery.tests.fluentlenium.datasource.DatasourceAddPage.FormField.*;
import static junit.framework.TestCase.fail;

@RunWith(Parameterized.class)
public class DatasourceAddValidationUiTest extends ChromeFluentTest {
    protected boolean onboardingFlow;

    public DatasourceAddValidationUiTest(boolean onboardingFlow) {
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

    @Page
    protected DatasourceAddPage page;

    @Before
    public void DatasourceAddValidationUiTest() {
        if (onboardingFlow) {
            System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
        }

        page.setOnboardingFlow(onboardingFlow);
        page.go();

        if (!page.isRendered()) {
            fail("Failed to render add datasource page");
        }
    }

    @Test
    public void testEmptyValues() {
        page.submitForm();

        for (FormField formField : FormField.values()) {
            if (formField == database) {
                continue;
            }

            if (formField != password) {
                if (formField == type) {
                    page.assertFormValidationMessagePresent(formField);
                } else {
                    page.assertFormValidationMessagePresent(formField);
                }
            }
        }
    }

    @Test
    public void testMySqlDatasourceValidation() {
        page.selectDatasourceType(MYSQL);

        page.submitForm();

        for (FormField formField : FormField.values()) {
            if (formField == database || formField == type) {
                continue;
            }

            if (formField != password) {
                page.assertFormValidationMessagePresent(formField);
            }
        }
    }

    @Test
    public void testPostgreSqlDatasourceValidation() {
        page.selectDatasourceType(POSTGRESQL);

        page.submitForm();

        for (FormField formField : FormField.values()) {
            if (formField == type) {
                continue;
            }

            if (formField != password) {
                page.assertFormValidationMessagePresent(formField);
            }
        }
    }

    @Test
    public void testRedshiftSqlDatasourceValidation() {
        page.selectDatasourceType(REDSHIFT);

        page.submitForm();

        for (FormField formField : FormField.values()) {
            if (formField == type) {
                continue;
            }

            if (formField != password) {
                page.assertFormValidationMessagePresent(formField);
            }
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
