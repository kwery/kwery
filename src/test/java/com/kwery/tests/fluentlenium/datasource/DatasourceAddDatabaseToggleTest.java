package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceAddDatabaseToggleTest extends ChromeFluentTest {
    NinjaServerRule ninjaServerRule = new NinjaServerRule();

    DatasourceAddPage page;

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Before
    public void setUpDatasourceAddDatabaseToggleTest() {
        page = createPage(DatasourceAddPage.class);
        page.withDefaultSearchWait(1, TimeUnit.SECONDS);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            TestCase.fail("Could not render add mySqlDatasource page");
        }
    }

    @Test
    public void test() {
        assertThat(page.isDatabaseFormFieldVisible(), is(false));
        page.selectDatasourceType(Datasource.Type.POSTGRESQL);
        page.waitForDatabaseFormFieldToBeVisible();
        page.selectDatasourceType(Datasource.Type.MYSQL);
        page.waitForDatabaseFormFieldToBeInvisible();
    }
}
