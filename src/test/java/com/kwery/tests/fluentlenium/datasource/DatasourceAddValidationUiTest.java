package com.kwery.tests.fluentlenium.datasource;

import com.kwery.tests.fluentlenium.datasource.DatasourceAddPage.FormField;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.Datasource.Type.POSTGRESQL;
import static com.kwery.models.Datasource.Type.REDSHIFT;
import static com.kwery.tests.fluentlenium.datasource.DatasourceAddPage.FormField.*;
import static com.kwery.tests.fluentlenium.datasource.DatasourceAddPage.INPUT_VALIDATION_ERROR_MESSAGE;
import static com.kwery.tests.fluentlenium.datasource.DatasourceAddPage.SELECT_VALIDATION_ERROR_MESSAGE;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatasourceAddValidationUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected DatasourceAddPage page;

    @Before
    public void DatasourceAddValidationUiTest() {
        page.go();

        if (!page.isRendered()) {
            fail("Failed to render add datasource page");
        }
    }

    @Test
    public void testEmptyValues() {
        page.submitForm();

        for (FormField formField : values()) {
            if (formField == database) {
                continue;
            }

            if (formField != password) {
                if (formField == type) {
                    page.waitForReportFormValidationMessage(formField, SELECT_VALIDATION_ERROR_MESSAGE);
                    assertThat(page.validationMessage(formField), is(SELECT_VALIDATION_ERROR_MESSAGE));
                } else {
                    page.waitForReportFormValidationMessage(formField, INPUT_VALIDATION_ERROR_MESSAGE);
                    assertThat(page.validationMessage(formField), is(INPUT_VALIDATION_ERROR_MESSAGE));
                }
            }
        }
    }

    @Test
    public void testMySqlDatasourceValidation() {
        page.selectDatasourceType(MYSQL);

        page.submitForm();

        for (FormField formField : values()) {
            if (formField == database || formField == type) {
                continue;
            }

            if (formField != password) {
                page.waitForReportFormValidationMessage(formField, INPUT_VALIDATION_ERROR_MESSAGE);
                assertThat(page.validationMessage(formField), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }
    }

    @Test
    public void testPostgreSqlDatasourceValidation() {
        page.selectDatasourceType(POSTGRESQL);

        page.submitForm();

        for (FormField formField : values()) {
            if (formField == type) {
                continue;
            }

            if (formField != password) {
                page.waitForReportFormValidationMessage(formField, INPUT_VALIDATION_ERROR_MESSAGE);
                assertThat(page.validationMessage(formField), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }
    }

    @Test
    public void testRedshiftSqlDatasourceValidation() {
        page.selectDatasourceType(REDSHIFT);

        page.submitForm();

        for (FormField formField : values()) {
            if (formField == type) {
                continue;
            }

            if (formField != password) {
                page.waitForReportFormValidationMessage(formField, INPUT_VALIDATION_ERROR_MESSAGE);
                assertThat(page.validationMessage(formField), is(INPUT_VALIDATION_ERROR_MESSAGE));
            }
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
