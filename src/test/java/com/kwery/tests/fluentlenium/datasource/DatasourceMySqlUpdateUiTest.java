package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.models.Datasource.Type.POSTGRESQL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.util.TestUtil.datasource;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceMySqlUpdateUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Page
    protected UpdateDatasourcePage page;

    protected Datasource mySqlDatasource;
    private Datasource anotherDatasource;

    @Before
    public void setUpUpdateDatasourcePageTest() {
        mySqlDatasource = mysqlDockerRule.getMySqlDocker().datasource();
        mySqlDatasource.setId(dbId());
        datasourceDbSetup(mySqlDatasource);

        anotherDatasource = datasource(POSTGRESQL);
        datasourceDbSetup(anotherDatasource);

        page.go(mySqlDatasource.getId());

        if (!page.isRendered()) {
            fail("Could not render update mySqlDatasource page");
        }
    }

    @Test
    public void testUpdateMySql() {
        page.waitForForm(DatasourceAddPage.FormField.label, mySqlDatasource.getLabel());

        List<String> fields = page.formFields();

        assertThat(fields.get(0), is(mySqlDatasource.getUrl()));
        assertThat(fields.get(2), is(String.valueOf(mySqlDatasource.getPort())));
        assertThat(fields.get(3), is(mySqlDatasource.getUsername()));
        assertThat(fields.get(4), is(mySqlDatasource.getPassword()));
        assertThat(fields.get(5), is(mySqlDatasource.getLabel()));

        String newLabel = "newLabel";
        page.fillLabel(newLabel);
        page.submit();

        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(newLabel, mySqlDatasource.getType());
    }

    @Test
    public void testLabelExists() {
        page.waitForForm(DatasourceAddPage.FormField.label, mySqlDatasource.getLabel());
        page.fillLabel(anotherDatasource.getLabel());
        page.submit();
        page.waitForFailureMessage(anotherDatasource.getLabel(), anotherDatasource.getType());
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
