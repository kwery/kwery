package com.kwery.tests.fluentlenium.datasource;

import com.google.common.collect.Lists;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
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
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;

public class DatasourceDeleteUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected DatasourceListPage page;

    protected Datasource datasource0;
    protected Datasource datasource1;
    private List<Datasource> datasources;
    private SqlQueryModel sqlQueryModel;

    @Before
    public void setUpDeleteDatasourcePageTest() {
        datasource0 = datasource();
        datasourceDbSetup(datasource0);

        datasource1 = datasource();
        datasourceDbSetup(datasource1);

        sqlQueryModel = sqlQueryModel(datasource1);
        sqlQueryDbSetUp(sqlQueryModel);

        datasources = Lists.newArrayList(datasource0, datasource1);
        datasources.sort(Comparator.comparing(Datasource::getId));

        page.go();
        page.waitForModalDisappearance();
    }

    @Test
    public void testSuccess() {
        Datasource toDeleteDatasource = null;
        for (int i = 0; i < datasources.size(); ++i) {
            toDeleteDatasource = datasources.get(i);
            if (!sqlQueryModel.getDatasource().getId().equals(toDeleteDatasource.getId())) {
                page.delete(i);
                break;
            }
        }

        page.waitForModalDisappearance();
        page.assertDeleteSuccessMessage(toDeleteDatasource.getLabel());

        for (Datasource datasource : datasources) {
            if (!datasource.getId().equals(toDeleteDatasource.getId())) {
                page.assertDatasourceList(0, page.toMap(datasource));
                break;
            }
        }

    }

    @Test
    public void testDeleteDatasourceWithSqlQuery() {
        Datasource toDeleteDatasource = null;
        for (int i = 0; i < datasources.size(); ++i) {
            toDeleteDatasource = datasources.get(i);
            if (sqlQueryModel.getDatasource().getId().equals(toDeleteDatasource.getId())) {
                page.delete(i);
                break;
            }
        }

        page.waitForModalDisappearance();
        page.assertDeleteFailureMessage(toDeleteDatasource.getLabel());

        for (int i = 0; i < datasources.size(); ++i) {
            page.assertDatasourceList(i, page.toMap(datasources.get(i)));
        }
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}

