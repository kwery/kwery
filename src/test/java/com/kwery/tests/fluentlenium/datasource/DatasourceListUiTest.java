package com.kwery.tests.fluentlenium.datasource;

import com.google.common.collect.Lists;
import com.kwery.models.Datasource;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Comparator;
import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.datasource;
import static org.junit.rules.RuleChain.outerRule;

public class DatasourceListUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected DatasourceListPage page;
    private List<Datasource> datasources;

    @Before
    public void setUpListDatasourcesPageTest() {
        Datasource datasource0 = datasource();
        datasourceDbSetup(datasource0);

        Datasource datasource1 = datasource();
        datasourceDbSetup(datasource1);

        datasources = Lists.newArrayList(datasource0, datasource1);
        datasources.sort(Comparator.comparing(Datasource::getId));

        goTo(page);
    }

    @Test
    public void test() {
        for (int i = 0; i < datasources.size(); ++i) {
            page.assertDatasourceList(i, page.toMap(datasources.get(i)));
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
