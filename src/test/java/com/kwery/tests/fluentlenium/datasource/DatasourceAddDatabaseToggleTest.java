package com.kwery.tests.fluentlenium.datasource;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.Datasource.Type.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

//TODO - Generate pairs and then toggle to cover all cases
public class DatasourceAddDatabaseToggleTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Page
    DatasourceAddPage page;

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Before
    public void setUpDatasourceAddDatabaseToggleTest() {
        page.go();

        if (!page.isRendered()) {
            TestCase.fail("Could not render add mySqlDatasource page");
        }
    }

    @Test
    public void testPostgresqlToMysqlToggle() {
        assertThat(page.isDatabaseFormFieldVisible(), is(false));
        page.selectDatasourceType(POSTGRESQL);
        page.waitForDatabaseFormFieldToBeVisible();
        page.selectDatasourceType(MYSQL);
        page.waitForDatabaseFormFieldToBeInvisible();
    }

    @Test
    public void testRedshiftToMysqlToggle() {
        assertThat(page.isDatabaseFormFieldVisible(), is(false));
        page.selectDatasourceType(REDSHIFT);
        page.waitForDatabaseFormFieldToBeVisible();
        page.selectDatasourceType(MYSQL);
        page.waitForDatabaseFormFieldToBeInvisible();
    }

    @Test
    public void testPostgresqlToRedshiftToggle() {
        assertThat(page.isDatabaseFormFieldVisible(), is(false));
        page.selectDatasourceType(POSTGRESQL);
        page.waitForDatabaseFormFieldToBeVisible();
        page.selectDatasourceType(REDSHIFT);
        assertThat(page.isDatabaseFormFieldVisible(), is(true));
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
