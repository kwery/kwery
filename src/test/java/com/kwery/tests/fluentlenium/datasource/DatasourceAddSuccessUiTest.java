package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.util.*;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.Datasource.Type.*;
import static org.junit.Assert.fail;

public class DatasourceAddSuccessUiTest extends ChromeFluentTest {
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
        page.go();

        if (!page.isRendered()) {
            fail("Add datasource page is not rendered");
        }
    }

    @Test
    public void testAddMySqlDatasource() {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        page.submitForm(datasource);
        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(datasource.getLabel(), MYSQL);
    }

    @Test
    public void testAddPostgreSqlDatasource() {
        Datasource datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();

        page.submitForm(datasource);
        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(datasource.getLabel(), POSTGRESQL);
    }

    @Test
    public void testAddRedshiftDatasource() {
        Datasource datasource = redshiftDockerRule.getRedshiftDocker().datasource();

        page.submitForm(datasource);
        page.waitForDatasourceListPage();
        page.waitForSuccessMessage(datasource.getLabel(), REDSHIFT);
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}

