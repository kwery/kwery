package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.PostgreSqlDockerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.util.Messages.UPDATE_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourcePostgreSqlUpdateUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public PostgreSqlDockerRule postgreSqlDockerRule = new PostgreSqlDockerRule();

    protected UpdateDatasourcePage page;

    protected Datasource postgreSqlDatasource;
    private Datasource anotherDatasource;

    @Before
    public void setUpUpdateDatasourcePageTest() {
        postgreSqlDatasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();
        postgreSqlDatasource.setId(dbId());
        datasourceDbSetup(postgreSqlDatasource);

        anotherDatasource = datasource(MYSQL);
        datasourceDbSetup(anotherDatasource);

        page = newInstance(UpdateDatasourcePage.class);
        page.setDatasourceId(postgreSqlDatasource.getId());

        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update mySqlDatasource page");
        }
    }

    @Test
    public void test() {
        page.waitForModalDisappearance();

        page.waitForForm(DatasourceAddPage.FormField.label, postgreSqlDatasource.getLabel());

        List<String> fields = page.formFields();

        assertThat(fields.get(0), is(postgreSqlDatasource.getUrl()));
        assertThat(fields.get(1), is(postgreSqlDatasource.getDatabase()));
        assertThat(fields.get(2), is(String.valueOf(postgreSqlDatasource.getPort())));
        assertThat(fields.get(3), is(postgreSqlDatasource.getUsername()));
        assertThat(fields.get(4), is(postgreSqlDatasource.getPassword()));
        assertThat(fields.get(5), is(postgreSqlDatasource.getLabel()));

        assertThat(page.actionLabel().toLowerCase(), is(UPDATE_M.toLowerCase()));

        String newLabel = "newLabel";
        page.fillLabel(newLabel);
        page.submit();

        page.waitForModalDisappearance();
        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(newLabel, postgreSqlDatasource.getType());
    }

    @Test
    public void testDuplicateLabel() {
        page.waitForModalDisappearance();
        page.fillLabel(anotherDatasource.getLabel());
        page.waitForForm(DatasourceAddPage.FormField.label, postgreSqlDatasource.getLabel());
        page.submit();
        page.waitForFailureMessage(anotherDatasource.getLabel(), anotherDatasource.getType());
    }
}
