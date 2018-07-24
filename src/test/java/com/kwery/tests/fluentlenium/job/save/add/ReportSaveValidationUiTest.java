package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.controllers.apis.OnboardingApiController;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage.ReportFormField;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage.SqlQueryFormField;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.assertj.FluentLeniumAssertions;
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

import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.ReportFormField.cronExpression;
import static com.kwery.tests.fluentlenium.job.save.ReportSavePage.ReportFormField.parentReportId;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static junit.framework.TestCase.fail;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;
import static org.junit.rules.RuleChain.outerRule;

@RunWith(Parameterized.class)
public class ReportSaveValidationUiTest extends ChromeFluentTest {
    protected boolean onboardingFlow;

    public ReportSaveValidationUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true},
                {false},
        });
    }

    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    ReportSavePage page;

    @Before
    public void setUpReportSaveValidationUiTest() {
        SmtpConfiguration smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);

        if (onboardingFlow) {
            System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, OnboardingApiController.TEST_ONBOARDING_VALUE);
        }

        page.setOnboardingFlow(onboardingFlow);
        goTo(page);

        if (!page.isRendered()) {
            fail("Save report page could not be rendered");
        }

        page.waitForModalDisappearance();
    }

    @Test
    public void testWithCronExpressionChosen() {
        page.chooseCronExpression();
        page.clickOnAddSqlQuery(0);
        page.submitReportSaveForm();
        page.assertNonEmptyReportFormValidationMessage();

        for (ReportFormField field : ReportFormField.values()) {
            if (field == parentReportId) {
                page.assertEmptyValidationMessage(field);
            } else {
                page.assertNonEmptyValidationMessage(field);
            }
        }

        sqlQueryFormValidation();
    }

    @Test
    public void testWithCronUiChosen() {
        page.chooseCronUi();
        page.clickOnAddSqlQuery(0);
        page.submitReportSaveForm();
        page.assertNonEmptyReportFormValidationMessage();

        for (ReportFormField field : ReportFormField.values()) {
            if (field == parentReportId || field == cronExpression) {
                page.assertEmptyValidationMessage(field);
            } else {
                page.assertNonEmptyValidationMessage(field);
            }
        }

        sqlQueryFormValidation();
    }

    @Test
    public void testWithParentReportChosen() {
        page.chooseParentReport();
        page.clickOnAddSqlQuery(0);
        page.submitReportSaveForm();
        page.assertNonEmptyReportFormValidationMessage();

        for (ReportFormField field : ReportFormField.values()) {
            if (field == cronExpression) {
                page.assertEmptyValidationMessage(field);
            } else {
                page.assertNonEmptyValidationMessage(field);
            }
        }

        sqlQueryFormValidation();
    }

    private void sqlQueryFormValidation() {
        for (int i = 0; i < 2; ++i) {
            for (SqlQueryFormField field : SqlQueryFormField.values()) {
                FluentLeniumAssertions.assertThat(el("div",
                        withClass().contains(String.format(".f-sql-query%d .%s-form-validation-message-f", i, field.name())),
                        withTextContent().notContains("")));
            }
        }


    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
