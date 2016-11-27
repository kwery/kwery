package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.tests.util.Messages.LABEL_VALIDATION_M;
import static com.kwery.tests.util.Messages.PORT_VALIDATION_M;
import static com.kwery.tests.util.Messages.URL_VALIDATION_M;
import static com.kwery.tests.util.Messages.USERNAME_VALIDATION_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatasourceAddValidationUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected DatasourceAddPage page;

    @Before
    public void DatasourceAddValidationUiTest() {
        page = createPage(DatasourceAddPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Failed to render add datasource page");
        }
    }

    @Test
    public void testEmptyValues() {
        page.submitForm("", "", "", "", "");
        assertThat(page.usernameValidationErrorMessage(), is(USERNAME_VALIDATION_M));
        assertThat(page.urlValidationErrorMessage(), is(URL_VALIDATION_M));
        assertThat(page.labelValidationErrorMessage(), is(LABEL_VALIDATION_M));
        assertThat(page.portValidationErrorMessage(), is(PORT_VALIDATION_M));
    }

    @Test
    public void testPortMinimumValue() {
        Datasource datasource = datasource();
        page.submitForm(datasource.getUrl(), String.valueOf(0), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        assertThat(page.portValidationErrorMessage(), is(PORT_VALIDATION_M));
    }
}
