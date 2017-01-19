package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.util.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.Datasource.Type.POSTGRESQL;
import static com.kwery.tests.util.Messages.CREATE_M;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DatasourceAddSuccessUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Rule
    public PostgreSqlDockerRule postgreSqlDockerRule = new PostgreSqlDockerRule();

    protected DatasourceAddPage page;

    @Before
    public void setUpAddDatasourceSuccessTest() {
        page = createPage(DatasourceAddPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Add datasource page is not rendered");
        }
    }

    @Test
    public void testAddMySqlDatasource() {
        assertThat(page.actionLabel().toLowerCase(), is(CREATE_M.toLowerCase()));

        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        page.submitForm(datasource);
        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(datasource.getLabel(), MYSQL);
    }

    @Test
    public void testAddPostgreSqlDatasource() {
        assertThat(page.actionLabel().toLowerCase(), is(CREATE_M.toLowerCase()));

        Datasource datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();

        page.submitForm(datasource);
        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(datasource.getLabel(), POSTGRESQL);
    }
}

