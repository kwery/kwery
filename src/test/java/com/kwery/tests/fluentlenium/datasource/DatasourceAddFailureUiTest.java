package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static com.kwery.tests.util.Messages.MYSQL_DATASOURCE_CONNECTION_FAILURE_M;
import static java.text.MessageFormat.format;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class DatasourceAddFailureUiTest extends ChromeFluentTest {
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected DatasourceAddPage page;

    protected Datasource datasource;

    @Before
    public void setUpAddDatasourceFailureTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(Datasource.TABLE)
                        .row()
                        .column(Datasource.COLUMN_ID, 1)
                        .column(Datasource.COLUMN_LABEL, datasource.getLabel())
                        .column(Datasource.COLUMN_PASSWORD, datasource.getPassword())
                        .column(Datasource.COLUMN_PORT, datasource.getPort())
                        .column(Datasource.COLUMN_TYPE, datasource.getType())
                        .column(Datasource.COLUMN_URL, datasource.getUsername())
                        .column(Datasource.COLUMN_USERNAME, datasource.getUsername())
                        .end()
                        .build()

        ).launch();

        page = createPage(DatasourceAddPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update datasource page");
        }
    }

    @Test
    public void test() {
        page.submitForm(datasource.getUrl() + "sjdfldsjf", String.valueOf(datasource.getPort()), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        page.waitForFailureMessage();
        assertThat(
                page.errorMessages(),
                containsInAnyOrder(
                        format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, datasource.getLabel()),
                        MYSQL_DATASOURCE_CONNECTION_FAILURE_M
                )
        );
    }
}
