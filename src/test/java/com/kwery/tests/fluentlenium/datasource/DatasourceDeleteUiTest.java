package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceDeleteUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected DatasourceListPage page;

    protected Datasource datasource0;
    protected Datasource datasource1;

    @Before
    public void setUpDeleteDatasourcePageTest() {
        datasource0 = datasource();
        datasourceDbSetup(datasource0);

        datasource1 = datasource();
        datasourceDbSetup(datasource1);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource1);
        sqlQueryDbSetUp(sqlQueryModel);

        page = newInstance(DatasourceListPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list datasources page");
        }
    }

    @Test
    public void testSuccess() {
        page.delete(0);
        page.waitForModalDisappearance();
        page.waitForDeleteSuccessMessage(datasource0.getLabel());
        List<List<String>> rows = page.rows();
        assertThat(rows, hasSize(1));
        assertThat(rows.get(0).get(0), is(datasource1.getLabel()));
    }

    @Test
    public void testDeleteDatasourceWithSqlQuery() {
        page.delete(1);
        page.waitForDeleteFailureSqlQueryMessage();
        page.waitForModalDisappearance();
        List<List<String>> rows = page.rows();
        assertThat(rows, hasSize(2));
        assertThat(rows.get(0).get(0), is(datasource0.getLabel()));
        assertThat(rows.get(1).get(0), is(datasource1.getLabel()));
    }
}

